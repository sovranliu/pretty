package com.slfuture.pretty.im.view.form;

import java.io.File;

import com.slfuture.carrie.base.type.List;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.FileResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.R;
import com.slfuture.pretty.general.view.form.ImageActivity;
import com.slfuture.pretty.im.Module;
import com.slfuture.pretty.im.utility.message.AudioMessage;
import com.slfuture.pretty.im.utility.message.VideoMessage;
import com.slfuture.pretty.im.utility.message.VoiceMessage;
import com.slfuture.pretty.im.utility.message.ImageMessage;
import com.slfuture.pretty.im.utility.message.TextMessage;
import com.slfuture.pretty.im.utility.message.core.IMessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;

/**
 * 对话消息窗口层
 */
@ResourceView(id = R.layout.fragment_chat_messages)
public class ChatMessagesFragment extends FragmentEx {
	/**
	 * 消息源接口
	 */
	public interface IChatMessageAdapter {
		/**
		 * 按钮本人头像
		 */
		public final static int BUTTON_PHOTO_LOCAL = 1;
		/**
		 * 按钮对方头像
		 */
		public final static int BUTTON_PHOTO_REMOTE = 2;


		/**
		 * 加载消息回调
		 * 
		 * @param messageId 指定截止消息ID，null表示最新消息
		 * @return 消息列表
		 */
		public List<IMessage> onLoad(String messageId);
		/**
		 * 头像点击回调
		 * 
		 * @param button 按钮类型
		 */
		public void onClick(int button);
	}

	/**
	 * 消息视图基类
	 */
	private abstract class ViewHolder {
		/**
		 * 头像
		 */
		public ImageView photo;
		/**
		 * 文本视图
		 */
		public ViewGroup layoutText;
		/**
		 * 图片视图
		 */
		public ViewGroup layoutImage;
		/**
		 * 短语音视图
		 */
		public ViewGroup layoutVoice;

		/**
		 * 渲染当前视图
		 * 
		 * @param message 消息对象
		 */
		public abstract void render(IMessage message);
	}

	/**
	 * 文字消息视图
	 */
	private class TextViewHolder extends ViewHolder {
		/**
		 * 文本
		 */
		public TextView text;

		/**
		 * 渲染当前视图
		 * 
		 * @param message 消息对象
		 */
		@Override
		public void render(IMessage message) {
			layoutText.setVisibility(View.VISIBLE);
			layoutImage.setVisibility(View.GONE);
			layoutVoice.setVisibility(View.GONE);
			//
			text.setText(((TextMessage)message).text);
		}
	}

	/**
	 * 图片消息视图
	 */
	private class ImageViewHolder extends ViewHolder {
		/**
		 * 图片
		 */
		public ImageView image;

		/**
		 * 渲染当前视图
		 */
		public void render(IMessage message) {
			layoutText.setVisibility(View.GONE);
			layoutImage.setVisibility(View.VISIBLE);
			layoutVoice.setVisibility(View.GONE);
			//
			ImageMessage imageMessage = (ImageMessage) message;
			if(null != imageMessage.thumbnail) {
				readerImage(imageMessage.thumbnail);
			}
			else if(null != imageMessage.thumbnailUrl) {
				Host.doImage("", new ImageResponse(imageMessage.thumbnailUrl, imageMessage) {
					@Override
					public void onFinished(Bitmap content) {
						((ImageMessage) tag).thumbnail = content;
						readerImage(content);
					}
				}, imageMessage.thumbnailUrl);
			}
			else if(null != imageMessage.original) {
				readerImage(imageMessage.original);
			}
			else if(null != imageMessage.originalFile) {
				imageMessage.original = readerImage(imageMessage.originalFile);
			}
			else if(null != imageMessage.originalUrl) {
				Host.doImage("", new ImageResponse(imageMessage.originalUrl, imageMessage) {
					@Override
					public void onFinished(Bitmap content) {
						((ImageMessage) tag).thumbnail = content;
						readerImage(content);
					}
				}, imageMessage.originalUrl);
			}
		}

		/**
		 * 渲染图片文件
		 * 
		 * @param file 图片文件
		 */
		public Bitmap readerImage(File file) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			int width = ChatMessagesFragment.this.getResources().getDisplayMetrics().widthPixels / 3;
			Bitmap result = null;
			if(options.outWidth < width) {
				result = GraphicsHelper.decodeFile(file, options.outWidth, options.outHeight);
			}
			else {
				result = GraphicsHelper.decodeFile(file, width, width * options.outHeight / options.outWidth);
			}
			if(null == result) {
				return null;
			}
			return readerImage(result);
		}

		/**
		 * 渲染位图对象
		 * 
		 * @param bitmap 位图对象
		 */
		public Bitmap readerImage(Bitmap bitmap) {
			final float scale = ChatMessagesFragment.this.getResources().getDisplayMetrics().density;
			LayoutParams params = image.getLayoutParams();
			int width = ChatMessagesFragment.this.getResources().getDisplayMetrics().widthPixels / 3;
			if(bitmap.getWidth() < width) {
				params.width = (int) (14  * scale + 0.5f) + bitmap.getWidth() + (int)(10 * scale + 0.5f);
				params.height = bitmap.getHeight() + (int)(10 * scale + 0.5f);
			}
			else {
				params.width = (int) (14  * scale + 0.5f) + width + (int)(10 * scale + 0.5f);
				params.height = width * bitmap.getHeight() / bitmap.getWidth() + (int)(10 * scale + 0.5f);
			}
			image.setLayoutParams(params);
			image.setImageBitmap(bitmap);
			return bitmap;
		}
	}

	/**
	 * 短语音消息视图
	 */
	private class VoiceViewHolder extends ViewHolder {
		/**
		 * 语音图标
		 */
		public ImageView imgIcon;
		/**
		 * 语音长度
		 */
		public TextView labLength;

		/**
		 * 渲染当前视图
		 */
		public void render(IMessage message) {
			layoutText.setVisibility(View.GONE);
			layoutImage.setVisibility(View.GONE);
			layoutVoice.setVisibility(View.VISIBLE);
			//
			if(IMessage.ORIENTATION_SEND == message.orientation()) {
				imgIcon.setImageResource(R.drawable.voice_right_stop);
			}
			else if(IMessage.ORIENTATION_RECEIVE == message.orientation()) {
				imgIcon.setImageResource(R.drawable.voice_left_stop);
			}
			VoiceMessage voiceMessage = (VoiceMessage) message;
			labLength.setText(String.valueOf(voiceMessage.length / 1000) + "'");
			if(null == voiceMessage.file) {
				Host.doFile("", new FileResponse(voiceMessage.url, voiceMessage) {
					@Override
					public void onFinished(File content) {
						VoiceMessage voiceMessage = (VoiceMessage) tag;
						voiceMessage.file = content;
					}
				}, voiceMessage.url);
			}
		}
	}

	/**
	 * 语音通话消息视图
	 */
	private class AudioViewHolder extends ViewHolder {
		/**
		 * 文本
		 */
		public TextView text;

		/**
		 * 渲染当前视图
		 * 
		 * @param message 消息对象
		 */
		@Override
		public void render(IMessage message) {
			layoutText.setVisibility(View.VISIBLE);
			layoutImage.setVisibility(View.GONE);
			layoutVoice.setVisibility(View.GONE);
			//
			text.setText(((AudioMessage) message).description());
		}
	}

	/**
	 * 视频通话消息视图
	 */
	private class VideoViewHolder extends ViewHolder {
		/**
		 * 文本
		 */
		public TextView text;

		/**
		 * 渲染当前视图
		 * 
		 * @param message 消息对象
		 */
		@Override
		public void render(IMessage message) {
			layoutText.setVisibility(View.VISIBLE);
			layoutImage.setVisibility(View.GONE);
			layoutVoice.setVisibility(View.GONE);
			//
			text.setText(((VideoMessage) message).description());
		}
	}

	/**
	 * 消息面板滚动
	 */
	public class MessagesOnScrollListener implements OnScrollListener {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) { }

		/**
		 * 滚动回调
		 */
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if(0 == totalItemCount) {
				return;
			}
			if(0 == firstVisibleItem) {  
                // 当前第一个元素
				scrollStatus = -1;
				return;
			}
            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {  
                 // 当前最后一个元素
            	scrollStatus = 1;
				return;
            }
            scrollStatus = 0;
		}
	}


	/**
	 * 消息面板触摸
	 */
	public class MessagesOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchRawY =  event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				if(-1 == scrollStatus && event.getRawY() > touchRawY + 20) {
					if(View.GONE == loading.getVisibility()) {
						loading.setVisibility(View.VISIBLE);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if(-1 == scrollStatus && event.getRawY() > touchRawY + 20) {
					// 加载聊天历史
//					if(View.VISIBLE == loading.getVisibility()) {
//						return false;
//					}
					String messageId = null;
					if(messageList.size() > 0) {
						messageId = messageList.get(0).id();
					}
					List<IMessage> list = chatMessageAdapter.onLoad(messageId);
					loading.setVisibility(View.GONE);
					if(0 == list.size()) {
						return false;
					}
					int i = 0;
					while(list.size() > i) {
						messageList.insert(i, ChatEmoticonFragment.convert(ChatMessagesFragment.this.getActivity(),  list.get(i)));
						i++;
					}
					messagesAdapter.notifyDataSetChanged();
					listMessages.setSelection(list.size() - 1);
				}
				break;
			}
			return false;
		}
	}


	/**
	 * 列表适配器
	 */
	public class MessagesAdapter extends BaseAdapter {
		/**
		 * 渲染器
		 */
        private LayoutInflater inflater;
        
        
        public MessagesAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return messageList.size();
        }
        @Override
        public Object getItem(int arg0) {
            return null;
        }
        @Override
        public long getItemId(int arg0) {
            return 0;
        }
        @Override
		public int getViewTypeCount() {
			return 10;
		}
        @Override
		public int getItemViewType(int position) {
        	IMessage message = messageList.get(position);
        	return (message.orientation() - 1) * 5 + message.type() - 1;
		}
        @SuppressLint("InflateParams")
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	IMessage message = messageList.get(position);
        	ViewHolder holder = null;
            if(null == convertView) {
            	switch(message.type()) {
            	case IMessage.TYPE_TEXT:
            		holder = new TextViewHolder();
            		break;
            	case IMessage.TYPE_IMAGE:
            		holder = new ImageViewHolder();
            		break;
            	case IMessage.TYPE_VOICE:
            		holder = new VoiceViewHolder();
            		break;
            	case IMessage.TYPE_AUDIO:
            		holder = new AudioViewHolder();
            		break;
            	case IMessage.TYPE_VIDEO:
            		holder = new VideoViewHolder();
            		break;
            	}
                if(IMessage.ORIENTATION_SEND == message.orientation()) {
                	convertView = inflater.inflate(R.layout.listitem_messages_right, null);
                    holder.photo = (ImageView)convertView.findViewById(R.id.messagesright_image_photo);
                    holder.layoutText = (ViewGroup) convertView.findViewById(R.id.messagesright_layout_text);
                    holder.layoutImage = (ViewGroup) convertView.findViewById(R.id.messagesright_layout_image);
                    holder.layoutVoice = (ViewGroup) convertView.findViewById(R.id.messagesright_layout_voice);
                }
                else if(IMessage.ORIENTATION_RECEIVE == message.orientation()) {
                	convertView = inflater.inflate(R.layout.listitem_messages_left, null);
                    holder.photo = (ImageView)convertView.findViewById(R.id.messagesleft_image_photo);
                    holder.layoutText = (ViewGroup) convertView.findViewById(R.id.messagesleft_layout_text);
                    holder.layoutImage = (ViewGroup) convertView.findViewById(R.id.messagesleft_layout_image);
                    holder.layoutVoice = (ViewGroup) convertView.findViewById(R.id.messagesleft_layout_voice);
                }
                holder.photo.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	if(null == chatMessageAdapter) {
	                		return;
	                	}
	                	// 展示原始图片
	                	if(IMessage.ORIENTATION_RECEIVE == (Integer) v.getTag()) {
	                    	chatMessageAdapter.onClick(IChatMessageAdapter.BUTTON_PHOTO_REMOTE);
	                	}
	                	else if(IMessage.ORIENTATION_SEND == (Integer) v.getTag()) {
	                    	chatMessageAdapter.onClick(IChatMessageAdapter.BUTTON_PHOTO_LOCAL);
	                	}
	                }
	            });
            	switch(message.type()) {
            	case IMessage.TYPE_TEXT:
            		TextViewHolder textHolder = (TextViewHolder) holder;
            		if(IMessage.ORIENTATION_SEND == message.orientation()) {
                		textHolder.text = (TextView) convertView.findViewById(R.id.messagesright_label_message);
                	}
            		else if(IMessage.ORIENTATION_RECEIVE == message.orientation()) {
                		textHolder.text = (TextView) convertView.findViewById(R.id.messagesleft_label_message);
                	}
            		break;
            	case IMessage.TYPE_IMAGE:
            		ImageViewHolder imageHolder = (ImageViewHolder) holder;
            		if(IMessage.ORIENTATION_SEND == message.orientation()) {
            			imageHolder.image = (ImageView) convertView.findViewById(R.id.messagesright_image_message);
                	}
            		else if(IMessage.ORIENTATION_RECEIVE == message.orientation()) {
            			imageHolder.image = (ImageView) convertView.findViewById(R.id.messagesleft_image_message);
                	}
            		// imageHolder.image.setTag(position);
            		imageHolder.image.setOnClickListener(new View.OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						if(null == v.getTag()) {
    							return;
    						}
    						ImageMessage imageMessage = (ImageMessage) messageList.get((Integer) v.getTag());
    						Intent intent = new Intent(ChatMessagesFragment.this.getActivity(), ImageActivity.class);
    						boolean sentry = false;
    						if(null != imageMessage.originalFile) {
    							intent.putExtra("path", imageMessage.originalFile.getAbsolutePath());
    							sentry = true;
    						}
    						else if(null != imageMessage.thumbnailFile) {
    							intent.putExtra("path", imageMessage.thumbnailFile.getAbsolutePath());
    							sentry = true;
    						}
    						if(null != imageMessage.originalUrl) {
    							intent.putExtra("url", imageMessage.originalUrl);
    							sentry = true;
    						}
    						if(sentry) {
        						ChatMessagesFragment.this.startActivity(intent);
    						}
    					}
            		});
            		break;
            	case IMessage.TYPE_VOICE:
            		VoiceViewHolder voiceHolder = (VoiceViewHolder) holder;
            		if(IMessage.ORIENTATION_SEND == message.orientation()) {
            			voiceHolder.imgIcon = (ImageView) convertView.findViewById(R.id.messagesright_image_voice);
            			voiceHolder.labLength = (TextView) convertView.findViewById(R.id.messagesright_label_voice);
                	}
            		else if(IMessage.ORIENTATION_RECEIVE == message.orientation()) {
            			voiceHolder.imgIcon = (ImageView) convertView.findViewById(R.id.messagesleft_image_voice);
            			voiceHolder.labLength = (TextView) convertView.findViewById(R.id.messagesleft_label_voice);
                	}
            		voiceHolder.imgIcon.setOnClickListener(new View.OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						if(null == v.getTag()) {
    							return;
    						}
    						VoiceMessage voiceMessage = (VoiceMessage) messageList.get((Integer) v.getTag());
    						if(null == voiceMessage.file) {
    							Toast.makeText(ChatMessagesFragment.this.getActivity(), "语音尚未下载结束", Toast.LENGTH_LONG).show();
    							return;
    						}
    						ImageView imageView = (ImageView) v;
    						if(null != playingView && playingView.getDrawable() instanceof AnimationDrawable) {
    							player.stop();
    							((AnimationDrawable) playingView.getDrawable()).stop();
    							if(IMessage.ORIENTATION_SEND == voiceMessage.orientation()) {
    								playingView.setImageResource(R.drawable.voice_right_stop);
    							}
    							else if(IMessage.ORIENTATION_RECEIVE == voiceMessage.orientation()) {
    								playingView.setImageResource(R.drawable.voice_left_stop);
    							}
    							if(playingView == imageView) {
    								return;
    							}
    							else {
    								playingView = null;
    							}
    						}
    						playingView = imageView;
    						if(IMessage.ORIENTATION_SEND == voiceMessage.orientation()) {
    							playingView.setImageResource(R.drawable.animation_voice_right);
    						}
    						else if(IMessage.ORIENTATION_RECEIVE == voiceMessage.orientation()) {
    							playingView.setImageResource(R.drawable.animation_voice_left);
    						}
    						((AnimationDrawable) playingView.getDrawable()).start();
    						try {
    							if(player.isPlaying()) {
    								player.pause();
    							}
    							player.reset();
    							player.setDataSource(voiceMessage.file.getAbsolutePath());
    							player.prepare();
    							player.start();
    						}
    						catch (Exception e) {
    							Log.e("pretty", "play voice failed", e);
    						}
    					}
            		});
            		break;
            	case IMessage.TYPE_AUDIO:
            		AudioViewHolder audioHolder = (AudioViewHolder) holder;
            		break;
            	case IMessage.TYPE_VIDEO:
            		VideoViewHolder videoHolder = (VideoViewHolder) holder;
            		break;
            	}
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(IMessage.TYPE_IMAGE == message.type()) {
            	((ImageViewHolder) holder).image.setTag(position);
            }
            else if(IMessage.TYPE_VOICE == message.type()) {
            	((VoiceViewHolder) holder).imgIcon.setTag(position);
            }
            holder.photo.setTag(message.orientation());
            Bitmap photo = Module.reactor.getPhoto(message.from());
            if(null != photo) {
            	holder.photo.setImageBitmap(photo);
            }
            holder.render(message);
            return convertView;
        }
    }
	
	
	/**
	 * 列表控件
	 */
	@ResourceView(id = R.id.chatmessages_listview_messages)
	public ListView listMessages;
	/**
	 * 数据列表
	 */
	private List<IMessage> messageList = new List<IMessage>();
	/**
	 * 消息源
	 */
	private IChatMessageAdapter chatMessageAdapter = null;


	/**
	 * 加载图
	 */
	private ImageView loading = null;
	/**
	 * 短语音播放器
	 */
	private MediaPlayer player = null;
	/**
	 * 正在播放短语音动画的视图
	 */
	private ImageView playingView = null;
	/**
	 * 适配器
	 */
	private MessagesAdapter messagesAdapter = null;
	/**
	 * 滚动状态
	 * -1：顶部，1：底部，0：其他
	 */
	private int scrollStatus = 0;
	/**
	 * 触摸状态
	 * 当前高度
	 */
	private float touchRawY = 0.0f;


	public IChatMessageAdapter adapter() {
		return chatMessageAdapter;
	}
	public void setAdapter(IChatMessageAdapter chatMessageAdapter) {
		this.chatMessageAdapter = chatMessageAdapter;
	}

	/**
	 * 添加消息
	 * 
	 * @param message 消息映射
	 */
	public void addMessage(IMessage message) {
		message = ChatEmoticonFragment.convert(this.getActivity(), message);
		messageList.add(message);
		messagesAdapter.notifyDataSetChanged();
        listMessages.setSelection(listMessages.getCount() - 1);
	}

	/**
	 * 添加消息列表
	 * 
	 * @param messages 消息映射列表
	 */
	public void addMessage(List<IMessage> messages) {
		for(IMessage message : messages) {
			message = ChatEmoticonFragment.convert(this.getActivity(), message);
			messageList.add(message);
		}
		messagesAdapter.notifyDataSetChanged();
        listMessages.setSelection(listMessages.getCount() - 1);
	}

	/**
	 * 界面创建
	 */
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
		//
    	player = new MediaPlayer();
    	player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(null != playingView && playingView.getDrawable() instanceof AnimationDrawable) {
					if(null == playingView.getTag()) {
						return;
					}
					IMessage message = messageList.get((Integer) playingView.getTag());
					((AnimationDrawable) playingView.getDrawable()).stop();
					if(IMessage.ORIENTATION_SEND == message.orientation()) {
						playingView.setImageResource(R.drawable.voice_right_stop);
					}
					else if(IMessage.ORIENTATION_RECEIVE == message.orientation()) {
						playingView.setImageResource(R.drawable.voice_left_stop);
					}
				}
				playingView = null;
			}
    	});
		RelativeLayout layout = new RelativeLayout(this.getActivity());
		layout.setGravity(RelativeLayout.CENTER_IN_PARENT);
		loading = new ImageView(this.getActivity());
		loading.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		loading.setScaleType(ScaleType.FIT_XY);
		loading.setImageResource(R.drawable.loading);
		layout.addView(loading);
		listMessages.addHeaderView(layout);
		listMessages.setOnScrollListener(new MessagesOnScrollListener());
		listMessages.setOnTouchListener(new MessagesOnTouchListener());
		loading.setVisibility(View.GONE);
		messageList.clear();
		if(null != chatMessageAdapter) {
			for(IMessage message : chatMessageAdapter.onLoad(null)) {
				messageList.add(ChatEmoticonFragment.convert(this.getActivity(), message));
			}
		}
		messagesAdapter = new MessagesAdapter(this.getActivity());
		listMessages.setAdapter(messagesAdapter);
		messagesAdapter.notifyDataSetChanged();
        listMessages.setSelection(listMessages.getCount() - 1);
	}

    @Override
    public void onDestroy() {
    	if(null != player) {
    		player.stop();
    		player = null;
    	}
    	super.onDestroy();
    }

	/**
	 * 是否附着到根视图
	 * 
	 * @return 是否附着到根视图
	 */
	@Override
	protected boolean attachToRoot() {
		return false;
	}
}

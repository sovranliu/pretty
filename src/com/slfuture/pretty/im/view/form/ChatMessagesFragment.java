package com.slfuture.pretty.im.view.form;

import java.io.File;

import com.slfuture.carrie.base.type.List;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.R;
import com.slfuture.pretty.im.Module;
import com.slfuture.pretty.im.utility.message.AudioMessage;
import com.slfuture.pretty.im.utility.message.VideoMessage;
import com.slfuture.pretty.im.utility.message.ImageMessage;
import com.slfuture.pretty.im.utility.message.TextMessage;
import com.slfuture.pretty.im.utility.message.core.IMessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
	 * 对话视图
	 */
	private class ViewHolder {
		/**
		 * 文本
		 */
		public TextView text;
		/**
		 * 头像
		 */
		public ImageView photo;
		/**
		 * 图片
		 */
		public ImageView image;


		/**
		 * 渲染当前视图
		 */
		public void render(IMessage message) {
			switch(message.type()) {
			case IMessage.TYPE_TEXT:
				text.setVisibility(View.VISIBLE);
				text.setText(((TextMessage)message).text);
				image.setVisibility(View.GONE);
				break;
			case IMessage.TYPE_IMAGE:
				text.setVisibility(View.GONE);
				image.setVisibility(View.VISIBLE);
				ImageMessage imageMessage = (ImageMessage) message;
				if(null != imageMessage.thumbnail) {
					readerImage(imageMessage.thumbnail);
				}
				else if(null != imageMessage.thumbnailUrl) {
					Host.doImage("", new ImageResponse(imageMessage.thumbnailUrl) {
						@Override
						public void onFinished(Bitmap content) {
							readerImage(content);
						}
					}, imageMessage.thumbnailUrl);
				}
				else if(null != imageMessage.original) {
					readerImage(imageMessage.original);
				}
				else if(null != imageMessage.originalFile) {
					readerImage(imageMessage.originalFile);
				}
				else if(null != imageMessage.originalUrl) {
					Host.doImage("", new ImageResponse(imageMessage.originalUrl) {
						@Override
						public void onFinished(Bitmap content) {
							readerImage(content);
						}
					}, imageMessage.originalUrl);
				}
				break;
			case IMessage.TYPE_SOUND:
				text.setVisibility(View.VISIBLE);
				text.setText("[短语音待处理...]");
				image.setVisibility(View.GONE);
				// TODO:短语音渲染处理
				break;
			case IMessage.TYPE_AUDIO:
				text.setVisibility(View.VISIBLE);
				text.setText(((AudioMessage) message).description());
				image.setVisibility(View.GONE);
				break;
			case IMessage.TYPE_VIDEO:
				text.setVisibility(View.VISIBLE);
				text.setText(((VideoMessage) message).description());
				image.setVisibility(View.GONE);
				break;
			}
		}
		
		/**
		 * 渲染图片文件
		 * 
		 * @param file 图片文件
		 */
		public void readerImage(File file) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			LayoutParams params = image.getLayoutParams();
			params.height = params.width * options.outHeight / options.outWidth;
			image.setLayoutParams(params);
			//
			image.setImageBitmap(GraphicsHelper.decodeFile(file, params.width, params.height));
		}

		/**
		 * 渲染位图对象
		 * 
		 * @param bitmap 位图对象
		 */
		public void readerImage(Bitmap bitmap) {
			LayoutParams params = image.getLayoutParams();
			params.height = params.width * bitmap.getHeight() / bitmap.getWidth();
			image.setLayoutParams(params);
			//
			image.setImageBitmap(bitmap);
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
					if(View.VISIBLE == loading.getVisibility()) {
						return false;
					}
					String messageId = null;
					if(messageList.size() > 0) {
						messageId = messageList.get(0).id();
					}
					List<IMessage> list = chatMessageAdapter.onLoad(messageId);
					loading.setVisibility(View.GONE);
					if(list.size() > 0) {
						messageList.add(list);
						listMessages.setSelection(list.size());
						messagesAdapter.notifyDataSetChanged();
					}
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
        @SuppressLint("InflateParams")
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	IMessage message = messageList.get(position);
        	ViewHolder holder = null;
            if(null == convertView) {
                holder = new ViewHolder();
                if(IMessage.ORIENTATION_SEND == message.orientation()) {
                	convertView = inflater.inflate(R.layout.listitem_messages_right, null);
                    holder.photo = (ImageView)convertView.findViewById(R.id.messagesright_image_photo);
                    holder.text = (TextView)convertView.findViewById(R.id.messagesright_label_message);
                    holder.image = (ImageView)convertView.findViewById(R.id.messagesright_image_message);
                }
                else if(IMessage.ORIENTATION_RECEIVE == message.orientation()) {
                	convertView = inflater.inflate(R.layout.listitem_messages_left, null);
                    holder.photo = (ImageView)convertView.findViewById(R.id.messagesleft_image_photo);
                    holder.text = (TextView)convertView.findViewById(R.id.messagesleft_label_message);
                    holder.image = (ImageView)convertView.findViewById(R.id.messagesleft_image_message);
                }
                Bitmap photo = Module.reactor.getPhoto(message.from());
                if(null != photo) {
                	holder.photo.setImageBitmap(photo);
                }
                holder.photo.setTag(message.orientation());
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
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
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
		messageList.add(message);
		messagesAdapter.notifyDataSetChanged();
	}

	/**
	 * 添加消息列表
	 * 
	 * @param messages 消息映射列表
	 */
	public void addMessage(List<IMessage> messages) {
		messageList.add(messages);
		messagesAdapter.notifyDataSetChanged();
	}

	/**
	 * 界面创建
	 */
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
		//
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
			messageList.add(chatMessageAdapter.onLoad(null));
		}
		messagesAdapter = new MessagesAdapter(this.getActivity());
		listMessages.setAdapter(messagesAdapter);
		messagesAdapter.notifyDataSetChanged();
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

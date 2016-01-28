package com.slfuture.pretty.im.view.form;

import com.slfuture.carrie.base.type.List;
import com.slfuture.carrie.base.type.Table;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.slfuture.pretty.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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
		 * @param messageId 指定截止消息ID，0表示最新消息
		 * @return 消息列表
		 */
		public List<Table<String, Object>> onLoad(int messageId);
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
		 * 头像
		 */
		public ImageView photo;
		/**
		 * 文本
		 */
		public TextView text;
		/**
		 * 图片
		 */
		public ImageView image;
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
					int messageId = 0;
					if(messageList.size() > 0) {
						messageId = (Integer) messageList.get(0).get("id");
					}
					List<Table<String, Object>> list = chatMessageAdapter.onLoad(messageId);
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
            Table<String, Object> map = messageList.get(position);
            final boolean isReceive = (Boolean) map.get("isReceive");
            Bitmap photo = (Bitmap) map.get("photo");
            String text = (String) map.get("text");
            Bitmap image = (Bitmap) map.get("image");
        	ViewHolder holder = null;
            if(null == convertView) {
                holder = new ViewHolder();
                if(!isReceive) {
                	convertView = inflater.inflate(R.layout.listitem_messages_right, null);
                    holder.photo = (ImageView)convertView.findViewById(R.id.messagesright_image_photo);
                    holder.text = (TextView)convertView.findViewById(R.id.messagesright_label_message);
                    holder.image = (ImageView)convertView.findViewById(R.id.messagesright_image_message);
                }
                else {
                	convertView = inflater.inflate(R.layout.listitem_messages_left, null);
                    holder.photo = (ImageView)convertView.findViewById(R.id.messagesleft_image_photo);
                    holder.text = (TextView)convertView.findViewById(R.id.messagesleft_label_message);
                    holder.image = (ImageView)convertView.findViewById(R.id.messagesleft_image_message);
                }
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.photo.setImageBitmap(photo);
            if(null == image) {
            	holder.text.setText(text);
            }
            else {
            	holder.image.setImageBitmap(image);
            }
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	if(null == chatMessageAdapter) {
                		return;
                	}
                	// 展示原始图片
                	if(isReceive) {
                    	chatMessageAdapter.onClick(IChatMessageAdapter.BUTTON_PHOTO_REMOTE);
                	}
                	else {
                    	chatMessageAdapter.onClick(IChatMessageAdapter.BUTTON_PHOTO_LOCAL);
                	}
                }
            });
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
	private List<Table<String, Object>> messageList = new List<Table<String, Object>>();
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
	public void addMessage(Table<String, Object> message) {
		messageList.add(message);
		messagesAdapter.notifyDataSetChanged();
	}

	/**
	 * 添加消息列表
	 * 
	 * @param messages 消息映射列表
	 */
	public void addMessage(List<Table<String, Object>> messages) {
		messageList.add(messages);
		messagesAdapter.notifyDataSetChanged();
	}

	/**
	 * 界面创建
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			messageList.add(chatMessageAdapter.onLoad(0));
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

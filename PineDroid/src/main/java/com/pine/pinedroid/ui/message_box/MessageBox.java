package com.pine.pinedroid.ui.message_box;

import static com.pine.pinedroid.utils.ContextExtKt.activityContext;
import static com.pine.pinedroid.utils.log.LogsKt.loge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.pine.pinedroid.R;
import com.pine.pinedroid.ui.message_box.style.AbsAlertDialog;
import com.pine.pinedroid.ui.message_box.style.SelectDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息框
 *
 * <pre>
 *
 * </pre>
 */
public class MessageBox implements OnClickListener, OnKeyListener,
        OnDismissListener {
    private static MessageBoxQueue queue = new MessageBoxQueue();
    private static Boolean isShowing = false;
    private Context context;
    private OnMessageClickListener listener;

    private TextView mainMassage;
    private TextView btn1;
    private TextView btn2;
    private TextView btn3;
    private TextView btn4;
    private AbsAlertDialog dialog;
    private Boolean cancelable = false;
    private WindowsStyle style = WindowsStyle.BlackNoFrame;
    private String message;
    private List<String> buttons;
    /**
     * 状态说明 1 ： 允许添加一个 2 ： 阻止所有添加请求 3 ： 自由添加
     */
    private int enableAddLock = 3;


    /**
     * 设置对话框是否能够被取消
     *
     * @param cancelable
     * @return
     */
    public MessageBox setCancelable(Boolean cancelable) {
        this.cancelable = cancelable;
        if (dialog != null) {
            dialog.setCancelable(cancelable);
        }
        return this;
    }


    private MessageBox(Context context) {
        this.context = context;
    }


    /**
     * 初始化
     *
     * <pre>
     * 必须将基础activity继承QFActivity/QFFragmentActivity
     * 才能用这个初始化
     * 否则 请传入上下文
     * </pre>
     *
     * @return
     */
    public static MessageBox i() {
        return new MessageBox(activityContext);
    }


    public static MessageBox i(Context context) {
        return new MessageBox(context);
    }


    /**
     * 通过这里显示提示框
     *
     * @param message
     */
    public void show(String message) {
        show(message, "确定");
    }


    /**
     * 通过这里显示提示框
     *
     * @param message
     */
    public void show(String message, String btn) {
        List<String> list = new ArrayList<String>();
        list.add(btn);
        show(message, list);
    }


    /**
     * 通过这里显示提示框
     *
     * @param message
     */
    public void show(String message, String btn1, String btn2) {
        List<String> list = new ArrayList<String>();
        list.add(btn1);
        list.add(btn2);
        show(message, list);
    }


    /**
     * 通过这里显示提示框
     *
     * @param message
     * @param buttons
     */
    public void show(String message, String btn1, String btn2, String btn3) {
        List<String> list = new ArrayList<String>();
        list.add(btn1);
        list.add(btn2);
        list.add(btn3);
        show(message, list);
    }


    /**
     * 通过这里显示提示框
     *
     * @param message
     * @param buttons
     */
    public void show(String message, String btn1, String btn2, String btn3,
                     String btn4) {
        List<String> list = new ArrayList<String>();
        list.add(btn1);
        list.add(btn2);
        list.add(btn3);
        list.add(btn4);
        show(message, list);
    }


    public WindowsStyle getStyle() {
        return style;
    }


    public MessageBox setStyle(WindowsStyle style) {
        this.style = style;
        return this;
    }


    // 清理前端窗口
    public void dismiss() {
        isShowing = false;
        queue.showNext();
        enableAddLock = 3;

        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }


    /**
     * 通过这里显示提示框
     *
     * @param message
     * @param buttons
     */
    public void show(String message, List<String> buttons) {
        this.message = message;
        this.buttons = buttons;
        /**
         * 状态说明 1 ： 允许添加一个 2 ： 阻止所有添加请求 3 ： 自由添加
         */
        if (enableAddLock == 3) {
            queue.getQueue().add(this);
            queue.showNext();
        } else if (enableAddLock == 1) {
            enableAddLock = 2;
            queue.getQueue().add(this);
            queue.showNext();
        }
    }


    /**
     * 本函数调用后，将会清理前端所有消息窗口，并且锁定MessageBox队列 直到本窗口结束，任何添加消息窗体操作将会直接被拒绝
     */
    public MessageBox cleanMessageQueueAndLock() {
        // 清空消息队列
        queue.getQueue().clear();
        // 清理前端窗口
        dismiss();
        // 锁定后续添加
        enableAddLock = 1;

        return this;
    }


    /**
     * 本函数运行后，如果最上层有其他弹出窗口，本次MessageBox将不会被压入消息队列，不会显示
     *
     * <pre>
     * 函数 cleanMessageQueueAndLock() 与 hideIfShowing() 互斥
     * 只能调用一个 请注意
     * </pre>
     *
     * @return
     */
    public MessageBox hideIfShowing() {
        // 锁定后续添加
        if (enableAddLock != 2) {
            enableAddLock = 4;
        }
        return this;
    }


    /**
     * 显示对话框
     *
     * <pre>
     * 立刻显示对话框
     * 本操作将会跳过消息队列 直接将对话框显示在屏幕上
     * 本操作会阻止消息队列中其他对话框的创建，在本对话框消失后其他对话框才会显示
     * 但是正在显示的对话框不会消失
     * </pre>
     *
     * @param message
     * @param buttons
     */
    public void showNow(String message, List<String> buttons) {
        if (message == null) {
            loge("提示信息不能为空");
            return;
        }
        if (buttons == null) {
            loge("按钮信息不能为null");
            return;
        }
        if (buttons.isEmpty()) {
            loge("按钮信息不能为0项");
            return;
        }
        if (context == null) {
            loge("消息框无法显示 - 上下文失效，将要显示的消息: " + message);
            return;
        }

        // 窗体风格设置
        if (style == WindowsStyle.BlackNoFrame) {
            dialog = new SelectDialog(context, R.style.dialog);
        }

        dialog.setCanceledOnTouchOutside(cancelable);// 设置点击Dialog外部任意区域关闭Dialog
        dialog.show();
        dialog.setOnDismissListener(this);
        isShowing = true;

        dialog.setOnKeyListener(this);

        AbsAlertDialog view = dialog.getView();

        mainMassage = (TextView) view.findViewById(R.id.mainMassage);
        btn1 = (TextView) view.findViewById(R.id.btn1);
        btn2 = (TextView) view.findViewById(R.id.btn2);
        btn3 = (TextView) view.findViewById(R.id.btn3);
        btn4 = (TextView) view.findViewById(R.id.btn4);

        mainMassage.setText(message);

        btn1.setVisibility(View.VISIBLE);
        btn2.setVisibility(View.GONE);
        btn3.setVisibility(View.GONE);
        btn4.setVisibility(View.GONE);

        btn1.setText(buttons.get(0));
        if (buttons.size() >= 2) {
            btn2.setText(buttons.get(1));
            btn2.setVisibility(View.VISIBLE);
        }
        if (buttons.size() >= 3) {
            btn3.setText(buttons.get(2));
            btn3.setVisibility(View.VISIBLE);
        }
        if (buttons.size() >= 4) {
            btn4.setText(buttons.get(3));
            btn4.setVisibility(View.VISIBLE);
        }

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
    }


    public OnMessageClickListener getListener() {
        return listener;
    }


    public MessageBox setListener(OnMessageClickListener listener) {
        this.listener = listener;
        return this;
    }


    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            if (v.getId() == R.id.btn1) {
                listener.messageBoxChoose(1);
            } else if (v.getId() == R.id.btn2) {
                listener.messageBoxChoose(2);
            } else if (v.getId() == R.id.btn3) {
                listener.messageBoxChoose(3);
            } else if (v.getId() == R.id.btn4) {
                listener.messageBoxChoose(4);
            }
        }
    }


    @Override
    public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (cancelable == false) {
                return true;
            } else {
                dismiss();
            }

        }

        return false;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public List<String> getButtons() {
        return buttons;
    }


    public void setButtons(List<String> buttons) {
        this.buttons = buttons;
    }


    public static Boolean getIsShowing() {
        return isShowing;
    }


    public static void setIsShowing(Boolean isShowing) {
        MessageBox.isShowing = isShowing;
    }


    /**
     * 窗口消失事件 - 主动回调
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();

    }

}

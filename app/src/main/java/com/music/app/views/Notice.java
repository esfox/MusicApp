package com.music.app.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.app.R;

@SuppressWarnings("unused")
public class Notice extends RelativeLayout implements View.OnClickListener
{
    private TextView noticeText;
    private ImageView noticeIcon;

    private boolean doNotDismiss = false;
    private boolean isClickable = true;
    private int duration = 3000;

    private int animationDuration = 200;

    public Notice(Context context)
    {
        super(context);
        initialize(context);
    }

    private void initialize(Context context)
    {
        View parent = inflate(context, R.layout.notice, this);
        RelativeLayout notice = (RelativeLayout) parent.findViewById(R.id.notice);
        notice.setOnClickListener(this);

        setGravity(Gravity.END | Gravity.TOP);
        setTag("Notice");

        noticeText = (TextView) parent.findViewById(R.id.notice_text);
        noticeIcon = (ImageView) parent.findViewById(R.id.notice_icon);
    }

    @Override
    public void onClick(View v)
    {
        if(isClickable)
            dismiss();
    }

    public void setNoticeText(String message)
    {
        noticeText.setText(message);
        invalidate();
        requestLayout();
    }

    /**
     * PASS 24DP ICONS ONLY */
    public void setNoticeIcon(int iconId)
    {
        noticeIcon.setImageResource(iconId);
    }

    public void doNotDismiss()
    {
        doNotDismiss = true;
    }

    public void setNotClickable()
    {
        isClickable = false;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public void setAnimationDuration(int animationDuration)
    {
        this.animationDuration = animationDuration;
    }

    public void setBelow()
    {
        setGravity(Gravity.END | Gravity.BOTTOM);
    }

    public void show()
    {
        removeDisplayedNotice();

        ((ViewGroup) ((Activity) getContext())
                .getWindow().getDecorView().getRootView()).addView(this);

        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.notice_show));

        if (!doNotDismiss)
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    dismiss();
                }
            }, duration);
    }

    public void dismiss()
    {
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.notice_dismiss));

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ((ViewGroup) ((Activity) getContext())
                        .getWindow().getDecorView().getRootView()).removeView(Notice.this);
            }
        }, animationDuration + 1);
    }

    private void removeDisplayedNotice()
    {
        ViewGroup rootView = ((ViewGroup) ((Activity) getContext())
                .getWindow().getDecorView().getRootView());

        Notice displayedNotice = (Notice) rootView.findViewWithTag("Notice");

        if(displayedNotice != null)
            rootView.removeView(displayedNotice);
    }
}

package com.zeekrlife.hicar.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Lei.Chen29
 */
public class RecycleGridDivider extends RecyclerView.ItemDecoration {

    private int space;
    private int color;
    private Paint mPaint;

    /**
     * 默认的，垂直方向 横纵1px 的分割线 颜色透明
     */
    public RecycleGridDivider() {
        this(1);
    }

    /**
     * 自定义宽度的透明分割线
     *
     * @param space 指定宽度
     */
    public RecycleGridDivider(int space) {
        this(space, Color.TRANSPARENT);
    }

    public RecycleGridDivider(Context context, int space, boolean isDp) {
        if (isDp) {
            float density = context.getResources().getDisplayMetrics().density;
            space = (int) (density * space);
        }
        this.space = space;
        this.color = Color.TRANSPARENT;
        initPaint();
    }

    /**
     * 自定义宽度，并指定颜色的分割线
     *
     * @param space 指定宽度
     * @param color 指定颜色
     */

    public RecycleGridDivider(int space, int color) {
        this.space = space;
        this.color = color;
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(space);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
        int span = manager.getSpanCount();
        int offset = space;
        //得到View的位置
        int childPosition = parent.getChildAdapterPosition(view);
        //第一排，顶部不画
        if (childPosition < span) {
            //最左边的，左边不画
            if (childPosition % span == 0) {
                outRect.set(0, space / 3, offset, 0);
                //最右边，右边不画
            } else if (childPosition % span == span - 1) {
                outRect.set(offset, space / 3, 0, 0);
            } else {
                outRect.set(offset, 0, offset, 0);
            }
        } else {
            //上下的分割线，就从第二排开始，每个区域的顶部直接添加设定大小，不用再均分了
            if (childPosition % span == 0) {
                outRect.set(0, space / 3, offset, 0);
            } else if (childPosition % span == span - 1) {
                outRect.set(offset, space / 3, 0, 0);
            } else {
                outRect.set(offset, space / 3, offset, 0);
            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }
}
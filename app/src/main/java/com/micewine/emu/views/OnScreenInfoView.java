package com.micewine.emu.views;

import static com.micewine.emu.activities.EmulationActivity.handler;
import static com.micewine.emu.activities.MainActivity.enableCpuCounter;
import static com.micewine.emu.activities.MainActivity.enableDebugInfo;
import static com.micewine.emu.activities.MainActivity.enableRamCounter;
import static com.micewine.emu.activities.MainActivity.memoryStats;
import static com.micewine.emu.activities.MainActivity.miceWineVersion;
import static com.micewine.emu.activities.MainActivity.selectedD3DXRenderer;
import static com.micewine.emu.activities.MainActivity.selectedDXVK;
import static com.micewine.emu.activities.MainActivity.selectedVKD3D;
import static com.micewine.emu.activities.MainActivity.selectedWineD3D;
import static com.micewine.emu.activities.MainActivity.totalCpuUsage;
import static com.micewine.emu.activities.MainActivity.vulkanDriverDeviceName;
import static com.micewine.emu.activities.MainActivity.vulkanDriverDriverVersion;
import static com.micewine.emu.core.RatPackageManager.getPackageNameVersionById;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.micewine.emu.R;

public class OnScreenInfoView extends View {
    public OnScreenInfoView(Context context) {
        super(context);
        init(context);
    }

    public OnScreenInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OnScreenInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private final Paint paint = new Paint();
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            handler.postDelayed(this, 800);
        }
    };

    private void init(Context context) {
        paint.setTextSize(40F);
        paint.setTypeface(context.getResources().getFont(R.font.quicksand));
        paint.setStrokeWidth(8F);

        handler.post(updateRunnable);
    }

    private final String vkd3dVersion = getPackageNameVersionById(selectedVKD3D);
    private final String dxvkVersion = getPackageNameVersionById(selectedDXVK);
    private final String wineD3DVersion = getPackageNameVersionById(selectedWineD3D);

    private float statsX = 20F;
    private float statsY = 0F;
    private float maxWidth = 0F;
    private float totalHeight = 0F;
    private boolean isDragging = false;
    private float dX, dY;

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float lineHeight = paint.getTextSize() + 10F;
        float currentY = statsY + lineHeight;
        float startY = currentY - lineHeight; // Top of the first line

        maxWidth = 0F;

        if (enableRamCounter) {
            String text = "RAM: " + memoryStats;
            drawText(text, statsX, currentY, canvas);
            currentY += lineHeight;
            maxWidth = Math.max(maxWidth, paint.measureText(text));
        }
        if (enableCpuCounter) {
            String text = "CPU: " + totalCpuUsage;
            drawText(text, statsX, currentY, canvas);
            currentY += lineHeight;
            maxWidth = Math.max(maxWidth, paint.measureText(text));
        }

        totalHeight = currentY - startY;

        if (enableDebugInfo) {
            onScreenInfo(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enableRamCounter && !enableCpuCounter)
            return false;

        float x = event.getX();
        float y = event.getY();
        float lineHeight = paint.getTextSize() + 10F;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Check if touch is within bounds of the stats block
                // statsX, statsY is top-left approximately (offset by lineHeight logic)
                // statsY points to the "base" Y before the first line is drawn?
                // In onDraw: currentY starts at statsY + lineHeight.
                // So the text draws from y = statsY + lineHeight (baseline).
                // The top of the text is roughly statsY + lineHeight - textSize.
                // Simplified text box hit detection:
                // X range: statsX +/- padding to statsX + maxWidth + padding
                // Y range: statsY to statsY + totalHeight

                // Let's assume statsY is the top of the block.
                if (x >= statsX && x <= statsX + maxWidth &&
                        y >= statsY && y <= statsY + totalHeight) {

                    dX = x - statsX;
                    dY = y - statsY;
                    isDragging = true;
                    return true;
                }
                return false;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    statsX = x - dX;
                    statsY = y - dY;
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    isDragging = false;
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(updateRunnable);
    }

    private void onScreenInfo(Canvas canvas) {
        float lineHeight = paint.getTextSize() + 10F;
        float currentY = lineHeight;

        drawText(miceWineVersion, getTextEndX(canvas, miceWineVersion), currentY, canvas);
        currentY += lineHeight;

        drawText(vkd3dVersion, getTextEndX(canvas, vkd3dVersion), currentY, canvas);
        currentY += lineHeight;

        if ("DXVK".equals(selectedD3DXRenderer)) {
            drawText(dxvkVersion, getTextEndX(canvas, dxvkVersion), currentY, canvas);
            currentY += lineHeight;
        } else if ("WineD3D".equals(selectedD3DXRenderer)) {
            drawText(wineD3DVersion, getTextEndX(canvas, wineD3DVersion), currentY, canvas);
            currentY += lineHeight;
        }

        drawText(vulkanDriverDeviceName, getTextEndX(canvas, vulkanDriverDeviceName), currentY, canvas);
        currentY += lineHeight;

        drawText(vulkanDriverDriverVersion, getTextEndX(canvas, vulkanDriverDriverVersion), currentY, canvas);
        // currentY += lineHeight;
    }

    private void drawText(String text, float x, float y, Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, x, y, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawText(text, x, y, paint);
    }

    private float getTextEndX(Canvas canvas, String text) {
        return canvas.getWidth() - paint.measureText(text) - 20F;
    }
}
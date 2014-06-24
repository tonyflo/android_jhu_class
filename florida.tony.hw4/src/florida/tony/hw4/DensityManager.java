package florida.tony.hw4;

import android.content.Context;

public class DensityManager {
 
    private static DensityManager instance = null;
    public static DensityManager getInstance(Context context){
        if(instance == null){
            instance = new DensityManager(context);
        }
        return instance;
    }
    
    private float scale = 1f;
    private DensityManager(Context context){
        scale = context.getResources().getDisplayMetrics().density;
    }
    
    public int DIPToPixels(float dip){
        return (int) (0.5 + (dip * scale));
    }
    
    public int DIPToPixels(int dip){
        return DIPToPixels((float) dip);
    }
    
    public int DIPToPixels(double dip){
        return DIPToPixels((float) dip);
    }
    
    public int pixelsToDIP(float pixels){
        return (int) (0.5 + (pixels / scale));
    }
    
    public int pixelsToDIP(int pixels){
        return pixelsToDIP((float) pixels);
    }
    
    public int pixelsToDIP(double pixels){
        return pixelsToDIP((float) pixels);
    }
}
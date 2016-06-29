package com.letv.filter.helper;

import com.letv.filter.common.GPUImageBrightnessFilter;
import com.letv.filter.common.GPUImageContrastFilter;
import com.letv.filter.common.GPUImageExposureFilter;
import com.letv.filter.common.GPUImageHueFilter;
import com.letv.filter.common.GPUImageSaturationFilter;
import com.letv.filter.common.GPUImageSharpenFilter;
import com.letv.filter.common.MagicImageAdjustFilter;
import com.letv.filter.common.base.GPUImageFilter;

/**
 * 
 * idea from  jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools.FilterAdjuster
 */
public class MagicFilterAdjuster {
	private final Adjuster<? extends GPUImageFilter> adjuster;

    public MagicFilterAdjuster(final GPUImageFilter filter) {
        if (filter instanceof GPUImageSharpenFilter) {
            adjuster = new SharpnessAdjuster().filter(filter);
        } else if (filter instanceof GPUImageContrastFilter) {
            adjuster = new ContrastAdjuster().filter(filter);
        } else if (filter instanceof GPUImageHueFilter) {
            adjuster = new HueAdjuster().filter(filter);
        } else if (filter instanceof GPUImageSaturationFilter) {
            adjuster = new SaturationAdjuster().filter(filter);
        } else if (filter instanceof GPUImageExposureFilter) {
            adjuster = new ExposureAdjuster().filter(filter);
        } else if (filter instanceof GPUImageBrightnessFilter) {
            adjuster = new BrightnessAdjuster().filter(filter);
        } else if (filter instanceof MagicImageAdjustFilter) {
            adjuster = new ImageAdjustAdjuster().filter(filter);
        } else {
            adjuster = null;
        }
    }

    public boolean canAdjust() {
        return adjuster != null;
    }
    
    public void adjust(final int percentage) {
        if (adjuster != null) {
            adjuster.adjust(percentage);
        }
    }
    
    private abstract class Adjuster<T extends GPUImageFilter> {
        private T filter;
        
        @SuppressWarnings("unchecked")
        public Adjuster<T> filter(final GPUImageFilter filter) {
            this.filter = (T) filter;
            return this;
        }

		public T getFilter() {
            return filter;
        }
        
        public abstract void adjust(int percentage);
        protected float range(final int percentage, final float start, final float end) {
            return (end - start) * percentage / 100.0f + start;
        }

        protected int range(final int percentage, final int start, final int end) {
            return (end - start) * percentage / 100 + start;
        }
    }

    private class SharpnessAdjuster extends Adjuster<GPUImageSharpenFilter> {
    	
        @Override
        public void adjust(final int percentage) {
            getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
        }
    }

    private class HueAdjuster extends Adjuster<GPUImageHueFilter> {
    	
        @Override
        public void adjust(final int percentage) {
        	getFilter().setHue(range(percentage, 0.0f, 360.0f));
        }
    }

    private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {
       	
        @Override
        public void adjust(final int percentage) {
            getFilter().setContrast(range(percentage, 0.0f, 4.0f));
        }
    }

    private class BrightnessAdjuster extends Adjuster<GPUImageBrightnessFilter> {
    	    	
        @Override
        public void adjust(final int percentage) {
            getFilter().setBrightness(range(percentage, -0.5f, 0.5f));
        }
    }
    
    private class SaturationAdjuster extends Adjuster<GPUImageSaturationFilter> {
    	
        @Override
        public void adjust(final int percentage) {
            getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
        }
    }
    
    private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {

        @Override
        public void adjust(final int percentage) {
            getFilter().setExposure(range(percentage, -2.0f, 2.0f));
        }
    }  
    
    private class ImageAdjustAdjuster extends Adjuster<MagicImageAdjustFilter> {

        @Override
        public void adjust(final int percentage) {
            
        }
        
        public void adjust(final int percentage, final int type) {
            switch (type) {
			case MagicFilterType.CONTRAST:
				getFilter().setContrast(range(percentage, 0.0f, 4.0f));
				break;
			case MagicFilterType.SHARPEN:
				getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
				break;
			case MagicFilterType.SATURATION:
				getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
				break;
			case MagicFilterType.EXPOSURE:
				getFilter().setExposure(range(percentage, -2.0f, 2.0f));
				break;
			case MagicFilterType.BRIGHTNESS:
				getFilter().setBrightness(range(percentage, -0.5f, 0.5f));
				break;
			case MagicFilterType.HUE:
				getFilter().setHue(range(percentage, 0.0f, 360.0f));
				break;
			default:
				break;
			}
        }
    }  
}

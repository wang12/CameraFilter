package com.letv.filter.helper;

import android.content.Context;

import com.letv.filter.common.GPUImageBrightnessFilter;
import com.letv.filter.common.GPUImageContrastFilter;
import com.letv.filter.common.GPUImageExposureFilter;
import com.letv.filter.common.GPUImageHueFilter;
import com.letv.filter.common.GPUImageSaturationFilter;
import com.letv.filter.common.GPUImageSharpenFilter;
import com.letv.filter.common.MagicAmaroFilter;
import com.letv.filter.common.MagicAntiqueFilter;
import com.letv.filter.common.MagicBeautyFilter;
import com.letv.filter.common.MagicBlackCatFilter;
import com.letv.filter.common.MagicBrannanFilter;
import com.letv.filter.common.MagicBrooklynFilter;
import com.letv.filter.common.MagicCalmFilter;
import com.letv.filter.common.MagicCoolFilter;
import com.letv.filter.common.MagicCrayonFilter;
import com.letv.filter.common.MagicEarlyBirdFilter;
import com.letv.filter.common.MagicEmeraldFilter;
import com.letv.filter.common.MagicEvergreenFilter;
import com.letv.filter.common.MagicFairytaleFilter;
import com.letv.filter.common.MagicFreudFilter;
import com.letv.filter.common.MagicHealthyFilter;
import com.letv.filter.common.MagicHefeFilter;
import com.letv.filter.common.MagicHudsonFilter;
import com.letv.filter.common.MagicImageAdjustFilter;
import com.letv.filter.common.MagicInkwellFilter;
import com.letv.filter.common.MagicKevinFilter;
import com.letv.filter.common.MagicLatteFilter;
import com.letv.filter.common.MagicLomoFilter;
import com.letv.filter.common.MagicN1977Filter;
import com.letv.filter.common.MagicNashvilleFilter;
import com.letv.filter.common.MagicNostalgiaFilter;
import com.letv.filter.common.MagicPixarFilter;
import com.letv.filter.common.MagicRiseFilter;
import com.letv.filter.common.MagicRomanceFilter;
import com.letv.filter.common.MagicSakuraFilter;
import com.letv.filter.common.MagicSierraFilter;
import com.letv.filter.common.MagicSketchFilter;
import com.letv.filter.common.MagicSkinWhitenFilter;
import com.letv.filter.common.MagicSunriseFilter;
import com.letv.filter.common.MagicSunsetFilter;
import com.letv.filter.common.MagicSutroFilter;
import com.letv.filter.common.MagicSweetsFilter;
import com.letv.filter.common.MagicTenderFilter;
import com.letv.filter.common.MagicToasterFilter;
import com.letv.filter.common.MagicValenciaFilter;
import com.letv.filter.common.MagicWaldenFilter;
import com.letv.filter.common.MagicWarmFilter;
import com.letv.filter.common.MagicWhiteCatFilter;
import com.letv.filter.common.MagicXproIIFilter;
import com.letv.filter.common.base.GPUImageFilter;

public class MagicFilterFactory{
	
	private static int mFilterType = MagicFilterType.NONE;	
	
	public static GPUImageFilter getFilters(int type, Context mContext){
		mFilterType = type;
		switch (type) {
		case MagicFilterType.WHITECAT:
			return new MagicWhiteCatFilter(mContext);
		case MagicFilterType.BLACKCAT:
			return new MagicBlackCatFilter(mContext);
		case MagicFilterType.BEAUTY:
			return new MagicBeautyFilter(mContext);
		case MagicFilterType.SKINWHITEN:
			//List<GPUImageFilter> filters = new ArrayList<GPUImageFilter>();	
			//filters.add(new MagicBilateralFilter(mContext));
			//filters.add(new MagicSkinWhitenFilter(mContext));
			//return new MagicBaseGroupFilter(filters);
			return new MagicSkinWhitenFilter(mContext);
		case MagicFilterType.ROMANCE:
			return new MagicRomanceFilter(mContext);
		case MagicFilterType.SAKURA:
			return new MagicSakuraFilter(mContext);
		case MagicFilterType.AMARO:
			return new MagicAmaroFilter(mContext);
		case MagicFilterType.WALDEN:
			return new MagicWaldenFilter(mContext);
		case MagicFilterType.ANTIQUE:
			return new MagicAntiqueFilter(mContext);
		case MagicFilterType.CALM:
			return new MagicCalmFilter(mContext);
		case MagicFilterType.BRANNAN:
			return new MagicBrannanFilter(mContext);
		case MagicFilterType.BROOKLYN:
			return new MagicBrooklynFilter(mContext);
		case MagicFilterType.EARLYBIRD:
			return new MagicEarlyBirdFilter(mContext);
		case MagicFilterType.FREUD:
			return new MagicFreudFilter(mContext);
		case MagicFilterType.HEFE:
			return new MagicHefeFilter(mContext);
		case MagicFilterType.HUDSON:
			return new MagicHudsonFilter(mContext);
		case MagicFilterType.INKWELL:
			return new MagicInkwellFilter(mContext);
		case MagicFilterType.KEVIN:
			return new MagicKevinFilter(mContext);
		case MagicFilterType.LOMO:
			return new MagicLomoFilter(mContext);
		case MagicFilterType.N1977:
			return new MagicN1977Filter(mContext);
		case MagicFilterType.NASHVILLE:
			return new MagicNashvilleFilter(mContext);
		case MagicFilterType.PIXAR:
			return new MagicPixarFilter(mContext);
		case MagicFilterType.RISE:
			return new MagicRiseFilter(mContext);
		case MagicFilterType.SIERRA:
			return new MagicSierraFilter(mContext);
		case MagicFilterType.SUTRO:
			return new MagicSutroFilter(mContext);
		case MagicFilterType.TOASTER2:
			return new MagicToasterFilter(mContext);
		case MagicFilterType.VALENCIA:
			return new MagicValenciaFilter(mContext);
		case MagicFilterType.XPROII:
			return new MagicXproIIFilter(mContext);
		case MagicFilterType.EVERGREEN:
			return new MagicEvergreenFilter(mContext);
		case MagicFilterType.HEALTHY:
			return new MagicHealthyFilter(mContext);
		case MagicFilterType.COOL:
			return new MagicCoolFilter(mContext);
		case MagicFilterType.EMERALD:
			return new MagicEmeraldFilter(mContext);
		case MagicFilterType.LATTE:
			return new MagicLatteFilter(mContext);
		case MagicFilterType.WARM:
			return new MagicWarmFilter(mContext);
		case MagicFilterType.TENDER:
			return new MagicTenderFilter(mContext);
		case MagicFilterType.SWEETS:
			return new MagicSweetsFilter(mContext);
		case MagicFilterType.NOSTALGIA:
			return new MagicNostalgiaFilter(mContext);
		case MagicFilterType.FAIRYTALE:
			return new MagicFairytaleFilter(mContext);
		case MagicFilterType.SUNRISE:
			return new MagicSunriseFilter(mContext);
		case MagicFilterType.SUNSET:
			return new MagicSunsetFilter(mContext);
		case MagicFilterType.CRAYON:
			return new MagicCrayonFilter(mContext);
		case MagicFilterType.SKETCH:
			return new MagicSketchFilter(mContext);
		case MagicFilterType.BRIGHTNESS:
			return new GPUImageBrightnessFilter();
		case MagicFilterType.CONTRAST:
			return new GPUImageContrastFilter();
		case MagicFilterType.EXPOSURE:
			return new GPUImageExposureFilter();
		case MagicFilterType.HUE:
			return new GPUImageHueFilter();
		case MagicFilterType.SATURATION:
			return new GPUImageSaturationFilter();
		case MagicFilterType.SHARPEN:
			return new GPUImageSharpenFilter();
		case MagicFilterType.IMAGE_ADJUST:
			return new MagicImageAdjustFilter();
		default:
			return null;
		}
	}
	
	public int getFilterType(){
		return mFilterType;
	}
}

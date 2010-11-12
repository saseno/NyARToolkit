package jp.nyatla.nyartoolkit.dev.rpf.tracker.nyartk;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.raster.NyARGrayscaleRaster;
import jp.nyatla.nyartoolkit.core.rasterfilter.NyARRasterFilter_Reverse;
import jp.nyatla.nyartoolkit.core.rasterfilter.NyARRasterFilter_Roberts;
import jp.nyatla.nyartoolkit.core.types.NyARBufferType;

/**
 * LowResolutionLabelingSamplerへの入力コンテナです。
 * 基本GS画像と、1/nサイズのエッジ検出画像を持ち、これらに対する同期APIとアクセサを定義します。
 */
public class NyARTrackerSource
{
	private int _rob_resolution;
	/**
	 * 反転RobertsFilter画像のインスタンス
	 */
	private NyARGrayscaleRaster _rbraster;
	private NyARGrayscaleRaster _base_raster;
	private NyARVectorReader_INT1D_GRAY_8 _vec_reader;
	
	private NyARGrayscaleRaster _rb_source;
	private NyARRasterFilter_Roberts _rfilter=new NyARRasterFilter_Roberts(NyARBufferType.INT1D_GRAY_8);
	private NyARRasterFilter_Reverse _nfilter=new NyARRasterFilter_Reverse(NyARBufferType.INT1D_GRAY_8);
	/**
	 * @param i_width
	 * ソース画像のサイズ
	 * @param i_height
	 * ソース画像のサイズ
	 * @param i_depth
	 * 解像度の深さ(1/(2^n))倍の画像として処理する。
	 * @param i_is_alloc
	 * ベースラスタのバッファを内部確保外部参照にするかのフラグです。
	 * trueの場合、バッファは内部に確保され、wrapBuffer関数が使用できなくなります。
	 * @throws NyARException
	 */
	public NyARTrackerSource(int i_width,int i_height,int i_depth,boolean i_is_alloc) throws NyARException
	{
		assert(i_depth>0);
		int div=(int)Math.pow(2,i_depth);
		this._rob_resolution=div;
		//主GSラスタ
		this._base_raster=new NyARGrayscaleRaster(i_width,i_height,NyARBufferType.INT1D_GRAY_8,i_is_alloc);
		//Roberts変換ラスタ
		this._rb_source=new NyARGrayscaleRaster(i_width/div,i_height/div,NyARBufferType.INT1D_GRAY_8, true);
		//Robertsラスタは最も解像度の低いラスタと同じ
		this._rbraster=new NyARGrayscaleRaster(i_width/div,i_height/div,NyARBufferType.INT1D_GRAY_8, true);
		this._vec_reader=new NyARVectorReader_INT1D_GRAY_8(this._base_raster,this._rbraster);
	}
	/**
	 * GS画像をセットし、syncSourceで内部画像を更新します。
	 * この関数を使ってセットした画像は、インスタンスから参照されます。
	 * @param i_ref_source
	 * @throws NyARException 
	 */
	public void wrapBuffer(NyARGrayscaleRaster i_ref_source) throws NyARException
	{
		//バッファのスイッチ
		this._base_raster.wrapBuffer(i_ref_source.getBuffer());
		syncSource();
	}
	
	/**
	 * GS画像と他の内部画像を同期させます。
	 * この関数は、NyARTrackerSourceへインスタンスを渡す前に、必ず一度実行してください。
	 * 但し、wrapBufferを実行したときは不要です。
	 * @param i_ref_source
	 * @throws NyARException
	 */
	public final void syncSource() throws NyARException
	{
		//GS->1/(2^n)NRBF
		//解像度を半分にしながらコピー
		NyARGrayscaleRaster.copy(this._base_raster,0,0,this._rob_resolution,this._rb_source);

		//最終解像度のエッジ検出画像を作成
		this._rfilter.doFilter(this._rb_source,this._rbraster);
		this._nfilter.doFilter(this._rbraster, this._rbraster);

	}
	/**
	 * 基本GS画像に対するVector読み取り機を返します。
	 * @return
	 */
	public NyARVectorReader_INT1D_GRAY_8 getBaseVectorReader()
	{
		return this._vec_reader;
	}

	/**
	 * エッジ画像を返します。
	 * @return
	 */
	public NyARGrayscaleRaster getEdgeRaster()
	{
		return this._rbraster;
	}
	/**
	 * 基準画像を返します。
	 * @return
	 */
	public NyARGrayscaleRaster getBaseRaster()
	{
		return this._base_raster;
	}
	

}

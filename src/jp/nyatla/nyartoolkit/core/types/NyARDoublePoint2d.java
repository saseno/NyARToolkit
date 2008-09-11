package jp.nyatla.nyartoolkit.core.types;

public class NyARDoublePoint2d
{
	public double x;
	public double y;
	/**
	 * 配列ファクトリ
	 * @param i_number
	 * @return
	 */
	public static NyARDoublePoint2d[] createArray(int i_number)
	{
		NyARDoublePoint2d[] ret=new NyARDoublePoint2d[i_number];
		for(int i=0;i<i_number;i++)
		{
			ret[i]=new NyARDoublePoint2d();
		}
		return ret;
	}
	public NyARDoublePoint2d()
	{
		this.x=0;
		this.y=0;
	}	
	public NyARDoublePoint2d(double i_x,double i_y)
	{
		this.x=i_x;
		this.y=i_y;
	}
}
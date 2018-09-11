package Restricted_DP;

import java.util.BitSet;

/**
 * Class used for data structure for storing (partial) tours for the restricted DP
 * extends BitSet so that can also create copy of an instance
 * @author Christianhollreiser
 *
 */
public class ArrayListSet extends BitSet
{

	private static final long serialVersionUID = 1L;

	public ArrayListSet()
	{
		super();
	}
	
	public ArrayListSet(ArrayListSet input)
	{
		super();
		this.or(input);
	}
	
}

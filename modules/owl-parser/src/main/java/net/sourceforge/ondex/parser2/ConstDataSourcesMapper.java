package net.sourceforge.ondex.parser2;

import java.util.stream.Stream;

import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.utils.DataSourcePrototype;
import net.sourceforge.ondex.core.utils.ONDEXElemWrapper;

/**
 * This works like {@link ConstDataSourceMapper}, but adapts to {@link DataSourcesMapper}.
 *
 * @author brandizi
 * <dl><dt>Date:</dt><dd>24 Jul 2017</dd></dl>
 *
 */
public class ConstDataSourcesMapper<S> extends ConstStreamMapper<S, DataSourcePrototype, DataSource>
  implements DataSourcesMapper<S>
{
	private ConstDataSourceMapper<S> helper = new ConstDataSourceMapper<> ();
	
	public ConstDataSourcesMapper () {
		this ( null );
	}

	public ConstDataSourcesMapper ( DataSourcePrototype value ) 
	{
		super ( value );
		this.setValue ( value );
	}

	@Override
	public Stream<DataSource> map ( S source, ONDEXGraph graph )
	{
		this.helper.setValue ( this.getValue () );
		return Stream.of ( helper.map ( source, graph ) );
	}

	@Override
	public DataSourcePrototype getValue ()
	{
		return this.helper.getValue ();
	}

	@Override
	public void setValue ( DataSourcePrototype value )
	{
		this.helper.setValue ( value );
	}	
}

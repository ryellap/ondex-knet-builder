package net.sourceforge.ondex.rdf.convert.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections15.map.HashedMap;
import org.apache.jena.query.QuerySolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import net.sourceforge.ondex.rdf.convert.support.freemarker.FreeMarkerHelper;
import uk.ac.ebi.utils.exceptions.ExceptionUtils;

/**
 * TODO: comment me!
 *
 * @author brandizi
 * <dl><dt>Date:</dt><dd>28 Sep 2018</dd></dl>
 *
 */
@Component ( "straightRelationHandler" )
public class StraightRelationHandler extends QuerySolutionHandler
{
	@Autowired @Qualifier ( "conceptIds" )
	private Map<String, Integer> conceptIds = new HashMap<> ( 50000 );

	@Override
	public void accept ( List<QuerySolution> sols )
	{
		FreeMarkerHelper tplHelper = this.getTemplateHelper ();
		Map<String, Object> data = tplHelper.getTemplateData ( null );
		
		// A list of relations, where each is a map with from/typeId/to keys
		List<Map<String, Object>> relations = 
		sols.stream ()
		.map ( sol -> 
		{
			String fromIri = sol.getResource ( "from" ).getURI ();
			String toIri = sol.getResource ( "to" ).getURI ();
			
			Integer fromId = this.conceptIds.get ( fromIri );
			Integer toId = this.conceptIds.get ( toIri );

			if ( fromId == null || toId == null ) ExceptionUtils.throwEx ( 
				IllegalArgumentException.class, 
				"Error with the input RDF, cannot find concept ID(s) for the relation <%s> <%s>", fromIri, toIri
			);
			
			Map<String, Object> relMap = new HashedMap<> ();
			relMap.put ( "fromId", fromId );
			relMap.put ( "typeId", sol.getLiteral ( "typeId" ).getString () );
			relMap.put ( "toId", toId );
			return relMap;
		})
		.collect ( Collectors.toList () );
		data.put ( "relations", relations );
		
		DataPreProcessor dpp = this.getDataPreProcessor ();
		if ( dpp != null ) dpp.accept ( null, data );
		tplHelper.processTemplate ( this.getOxlTemplateName (), this.getOutWriter (), data );
	}
}

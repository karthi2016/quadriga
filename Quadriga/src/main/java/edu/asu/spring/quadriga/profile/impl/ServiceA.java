package edu.asu.spring.quadriga.profile.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.spring.quadriga.profile.ISearchResult;
import edu.asu.spring.quadriga.profile.IService;
import edu.asu.spring.quadriga.profile.IServiceRegistry;


@Service
public class ServiceA implements IService {

	String id;
	String name;
	
	@Autowired
	IServiceRegistry serviceRegistry;
	
	@Autowired
	ISearchResult searchResult;
	
	
	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return "edu.asu.ServiceA";
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}

	@Override
	public String getName() {
		return "ServiceA";
	}
	
	@Override
	public List<ISearchResult> search(String word) {
		
		List<ISearchResult> searchResults = new ArrayList<ISearchResult>();
		searchResult.setDescription("a smooth-textured sausage of minced beef or pork usually smoked; often served on a bread roll");
		searchResults.add(searchResult);
		
		return searchResults;
	}

	
	
	
}
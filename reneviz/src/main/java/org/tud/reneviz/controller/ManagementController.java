package org.tud.reneviz.controller;

import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tud.reneviz.service.JenaService;

@Named("management")
@ApplicationScoped
public class ManagementController {

	@Inject
	private JenaService jenaService;
	
	public void createCache() {
		jenaService.cacheAll();
	}
	
	public void loadDatabase() {

		jenaService.loadDatabase();
	}

}

package com.googlecode.semrs.server.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;

import com.googlecode.semrs.model.Encounter;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.service.EncounterService;

public class DeletePendingEncountersJob implements StatefulJob {
	
	
	private static final Logger LOG = Logger.getLogger(DeletePendingEncountersJob.class);

	@Override
	public void execute(JobExecutionContext context) {
		SchedulerContext skedCtx;
		try {
			skedCtx = context.getScheduler().getContext();
			EncounterService encounterService = (EncounterService)skedCtx.get("encounterService");
			synchronized(this){
				Date today = new Date();
				DateFormat dfm = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
				Map params = new HashMap();
				params.put("beforeDate", dfm.format(today));
				params.put("current", "true");
				LOG.info("Beginning Execution of DeletePendingEncountersJob");
				final Collection<Encounter> encounters = encounterService.listEncountersByQuery(params, "", "", "", "");
				LOG.info("Found " + encounters.size() + " Encounters");
				if(encounters.size()>0){
					for(Encounter encounter : encounters){
						if(encounter.isCurrent() && encounter.getEncounterDate().before(today)){
							try {
								encounterService.deleteEncounter(encounter);
							} catch (DeleteException e) {
								LOG.info("Error deleting encounter " + encounter.getId() + " caused by " + e.toString());
							}
						}
					}
				}else{
					LOG.info("Nothing to delete");
				}
			}
			LOG.info("Ending execution");
		} catch (SchedulerException e1) {
			LOG.error("Error executing job caused by " + e1);
		}
	}
}

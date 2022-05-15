package com.jefferson.laundryorderingsystem;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.TimeZone;

import com.jefferson.laundryorderingsystem.utils.CleanDatabaseJob;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LaundryOrderingSystemApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(LaundryOrderingSystemApplication.class, args);
		// clean up old data
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.start();
		JobDetail jobDetail = JobBuilder.newJob(CleanDatabaseJob.class).build();
		Trigger trigger = newTrigger().startNow().withSchedule(cronSchedule("* 18 * * * ?").inTimeZone(TimeZone.getTimeZone("Asia/Taipei"))).build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

}

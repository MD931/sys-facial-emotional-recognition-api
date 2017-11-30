package com.surirobot.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.surirobot.interfaces.IProcessPicture;
import com.surirobot.task.Task;
import com.surirobot.utils.Emotion;

/*
 * Class permettant de traiter la liste des images reçues en Base64
 */

public class ProcessPicture implements IProcessPicture{

	private static final Logger logger = LogManager.getLogger();

	/*
	 * (non-Javadoc)
	 * @see com.surirobot.interfaces.IProcess#process(java.lang.Object)
	 * 
	 * Méthode qui crée des threads et récupère le résultat de chaque thread
	 * et fait la moyenne des scores 
	 */
	
	@Override
	public String process(List<String> data) {
		logger.info("ProcessPicture : start process");

		if(data.size()<1) return "{}";

		List<Callable<JSONObject>> tasks = new ArrayList<>();
		data.forEach(e ->{
			tasks.add(new Task(e));
		});

		ExecutorService executor = Executors.newFixedThreadPool(data.size());
		CompletionService<JSONObject> completionService =
				new ExecutorCompletionService<JSONObject>(executor);

		List<JSONObject> scores = new ArrayList<>();
		List<Future<JSONObject>> futures = new ArrayList<Future<JSONObject>>();
		try {
			for(Callable<JSONObject> task : tasks)
				futures.add(completionService.submit(task));

			for(int i = 0; i<tasks.size();i++) 
				scores.add(completionService.take().get());

		} catch (InterruptedException | ExecutionException e) {
			logger.error("Interruption Execution Thread...\n"+e.getStackTrace());
		}finally{
			executor.shutdown();
		};
		return average(scores).toString();
	}

	/*
	 * Méthode qui fait la moyenne des scores
	 */
	public static JSONObject average(List<JSONObject> scores) {
		logger.error("ProcessPicture : start Average");

		JSONObject result = new JSONObject();
		for(Emotion e : Emotion.values()) 
			result.put(e.toString().toLowerCase(),0.0);

		scores.forEach(score -> {
			if(score != null && score.length() != 0)
				for(Emotion e : Emotion.values()) {
					//System.out.println("AVERAGE ====" +score);
					result.put(e.toString().toLowerCase(),
							result.getDouble(e.toString().toLowerCase())+score.getJSONObject("scores").optDouble(e.toString().toLowerCase(),0.0));
				}
		});
		
		if(scores.size()>0)
		for(Emotion e : Emotion.values())
			result.put(e.toString().toLowerCase(),result.getDouble(e.toString().toLowerCase())/scores.size());

		return new JSONObject().put("scores", result);
	}

}

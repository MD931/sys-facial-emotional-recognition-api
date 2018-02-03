package com.surirobot.interfaces;

import java.util.List;

import com.surirobot.task.Task;
/**
 * 
 * @author jussieu
 *
 * Cette Interface permet de traiter les donées reçues du client,
 * et de deleguer le travail au <code>{@link Task}</code>,
 * ensuite elle permet de passer le résultat au <code>{@link Par}</code>
 */
public interface IProcessPicture2 extends IProcess<String>{
	
}
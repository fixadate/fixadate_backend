package com.fixadate.domain.main.dto;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.entity.ToDo;
import com.fixadate.domain.adate.entity.ToDoStatus;
import java.time.format.DateTimeFormatter;

public record TodoInfo(
	Long id,
	String title,
	ToDoStatus toDoStatus,
	String date,
	boolean checked
	){
		public static TodoInfo of(ToDo todo) {
			return new TodoInfo(
				todo.getId(),
				todo.getTitle(),
				todo.getToDoStatus(),
				todo.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
				todo.isChecked()
			);
		}
	}

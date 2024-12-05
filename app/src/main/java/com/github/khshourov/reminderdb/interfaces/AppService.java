package com.github.khshourov.reminderdb.interfaces;

import com.github.khshourov.reminderdb.engine.responsehandlers.ResponseHandler;
import com.github.khshourov.reminderdb.lib.remindstore.RemindStore;

public interface AppService extends RemindStore, ResponseHandler, TimeService {}

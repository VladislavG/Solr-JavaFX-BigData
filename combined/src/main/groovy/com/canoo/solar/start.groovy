package com.canoo.solar

import org.opendolphin.core.client.comm.BlindCommandBatcher
import org.opendolphin.core.client.comm.InMemoryClientConnector
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler
import org.opendolphin.core.comm.DefaultInMemoryConfig
import com.canoo.solar.MainRegistrarAction
import com.canoo.solar.MainView

def config = new DefaultInMemoryConfig()

def batcher = new BlindCommandBatcher(deferMillis: 400, mergeValueChanges: true)
config.clientDolphin.clientConnector = new InMemoryClientConnector(config.clientDolphin, batcher)

//config.clientDolphin.clientConnector.sleepMillis = 100
config.clientDolphin.clientConnector.serverConnector = config.getServerDolphin().serverConnector


config.clientDolphin.clientConnector.uiThreadHandler = new JavaFXUiThreadHandler()
config.serverDolphin.registerDefaultActions()

config.getServerDolphin().register(new ApplicationDirector());
com.canoo.solar.Application.clientDolphin = config.getClientDolphin();

Application.launch(com.canoo.solar.Application.class);




package com.canoo.solar;

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.DataCommand
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.comm.NamedCommandHandler

class FullDataRequestCommandHandler implements NamedCommandHandler {



    public FullDataRequestCommandHandler() {
    }

    @Override
    void handleCommand(NamedCommand command, List<Command> response) {
        for (int i=10; i < 40; i++) {
            response.add(new DataCommand(new HashMap(id: i)))
        }
    }
}

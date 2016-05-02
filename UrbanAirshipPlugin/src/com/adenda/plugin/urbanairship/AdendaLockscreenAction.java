package com.adenda.plugin.urbanairship;

import android.os.Bundle;

import com.urbanairship.UAirship;
import com.urbanairship.actions.Action;
import com.urbanairship.actions.ActionArguments;
import com.urbanairship.actions.ActionRunRequest;
import com.urbanairship.actions.AddCustomEventAction;
import com.urbanairship.analytics.CustomEvent;
import com.urbanairship.push.PushMessage;

import java.util.HashMap;

public class AdendaLockscreenAction extends AddCustomEventAction {
	
	private static final String ACTION_NAME = "AdendaLockscreenAction";
	private static final String ADENDA_LOCKSCREEN_FOLLOW_ACTION = "Adenda lock screen follow event";

	public static void requestRun(final PushMessage message, final String sActionUri)
	{
		HashMap<String, Object> customEventData = new HashMap<String, Object>();
		customEventData.put(CustomEvent.EVENT_NAME, ADENDA_LOCKSCREEN_FOLLOW_ACTION);
		
		Bundle metadata = new Bundle();
		if (message != null)		
			metadata.putParcelable(ActionArguments.PUSH_MESSAGE_METADATA, message);
		
		ActionRunRequest.createRequest(AdendaLockscreenAction.ACTION_NAME).setSituation(Action.SITUATION_WEB_VIEW_INVOCATION).setValue(customEventData).setMetadata(metadata).run();
	}
	
	public static void register()
	{
		Action lockscreenAction = new AdendaLockscreenAction();
		UAirship.shared().getActionRegistry().registerAction( lockscreenAction, AdendaLockscreenAction.ACTION_NAME);
	}
}

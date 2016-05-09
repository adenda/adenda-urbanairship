package com.adenda.plugin.urbanairship;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.urbanairship.UAirship;
import com.urbanairship.analytics.CustomEvent;
import com.urbanairship.push.PushMessage;
import com.urbanairship.widget.UAWebView;
import com.urbanairship.widget.UAWebViewClient;

import sdk.adenda.lockscreen.fragments.AdendaFragmentInterface;
import sdk.adenda.widget.DateTimeFragment;

public class UrbanAirshipLockScreenFragment extends Fragment implements AdendaFragmentInterface {

    protected static final String NOTIFICATION_URL = "notification_url";
    protected static final String NOTIFICATION_ID = "notification_id";
    protected static final String UA_PUSH_MESSAGE = "ua_push_message";

    private static final int DEFAULT_DATE_TIME_TXT_COLOR = 0xFF000000;
    private static final int DEFAULT_BACKGROUND_COLOR = 0XFFFFFFFF;

    private static final String ADENDA_DATETIME_COLOR_PARAM = "adenda_datetime_color";
    private static final String ADENDA_BKGRD_COLOR_PARAM = "adenda_background_color";
    private static final String ADENDA_ACTION_URI = "adenda_action_uri";
    private static final String ADENDA_EXPAND_CONTENT = "adenda_expand_content";
    private static final String ADENDA_HIDE_DATETIME = "adenda_hide_datetime";

    private static final String ADENDA_OPEN_EVENT = "ADENDA_OPEN_EVENT";
    private static final String ADENDA_DISPLAY_EVENT = "ADENDA_DISPLAY_EVENT";
    private static final String ADENDA_ACTION_URI_EVENT_PROPERTY = "ADENDA_ACTION_URI_PROPERTY";

    private String notificationUrl;
    private PushMessage pushMessage;
    private String actionUri;
    private int notificationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            pushMessage = args.getParcelable(UA_PUSH_MESSAGE);
            notificationUrl = args.getString(NOTIFICATION_URL);
            notificationId = args.getInt(NOTIFICATION_ID, -1);
            actionUri = pushMessage == null ? null : pushMessage.getPushBundle().getString(ADENDA_ACTION_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (pushMessage == null) {
            return null;
        }

        // Load layout
        View view = inflater.inflate(R.layout.landing_page, container, false);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
        UAWebView webView = (UAWebView) view.findViewById(android.R.id.primary);

        // Set background color
        View dateTimeContainer = view.findViewById(R.id.date_time_container);
        dateTimeContainer.setBackgroundColor(PushUtils.parseColorExtra(pushMessage, ADENDA_BKGRD_COLOR_PARAM, DEFAULT_BACKGROUND_COLOR));

        if (PushUtils.parseBooleanExtra(pushMessage, ADENDA_HIDE_DATETIME, false)) {
            int dateTimeColor = PushUtils.parseColorExtra(pushMessage, ADENDA_DATETIME_COLOR_PARAM, DEFAULT_DATE_TIME_TXT_COLOR);

            // Add date/time fragment
            DateTimeFragment dateTimeFragment = DateTimeFragment.newInstance(DateTimeFragment.TXT_CENTER_JUSTIFY, dateTimeColor, true, false);
            getChildFragmentManager().beginTransaction().replace(R.id.date_time_container, dateTimeFragment).commit();
        }

        if (!PushUtils.parseBooleanExtra(pushMessage, ADENDA_EXPAND_CONTENT, false)) {
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.ua_content_container);
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) frameLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            frameLayout.setLayoutParams(layoutParams);
            // Make sure date/time fragment is on top and transparent
            dateTimeContainer.setBackgroundColor(Color.TRANSPARENT);
            dateTimeContainer.bringToFront();
        }

        // Load actual notification!
        if (notificationUrl != null && webView != null && progressBar != null) {
            webView.setWebViewClient(new UAWebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // Make sure to call through to the super's implementation
                    // or the javascript native bridge will not be fully loaded
                    super.onPageFinished(view, url);
                    // Hide the progress bar when the page is loaded
                    progressBar.setVisibility(View.GONE);
                }
            });

            webView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    UrbanAirshipLockScreenFragment.this.getActivity().onTouchEvent(event);
                    return false;
                }
            });

            // Load URL
            webView.loadUrl(notificationUrl);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Record display event
        recordEvent(ADENDA_DISPLAY_EVENT);

        // Dismiss notification if available
        if (notificationId > 0) {
            NotificationManagerCompat.from(getContext()).cancel(notificationId);
        }
    }

    @Override
    public boolean expandOnRotation() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Intent getActionIntent() {
        // Record open event
        recordEvent(ADENDA_OPEN_EVENT);

        if (!TextUtils.isEmpty(actionUri)) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(actionUri));
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            return i;
        }

        return null;
    }


    @Override
    public boolean coverEntireScreen() {
        return true;
    }

    @Override
    public Pair<Integer, Integer> getGlowpadResources() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getStartHelperForResult() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onActionFollowedAndLockScreenDismissed() {
        // TODO Auto-generated method stub
    }

    /**
     * Records a {@link CustomEvent}.
     *
     * @param eventName The event name.
     */
    private void recordEvent(@NonNull String eventName) {
        CustomEvent.Builder builder = new CustomEvent.Builder(eventName);

        if (pushMessage != null) {
            builder.setAttribution(pushMessage);
        }

        if (!TextUtils.isEmpty(actionUri)) {
            builder.addProperty(ADENDA_ACTION_URI_EVENT_PROPERTY, actionUri);
        }

        UAirship.shared().getAnalytics().addEvent(builder.create());
    }
}

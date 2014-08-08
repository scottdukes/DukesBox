package net.scottdukes.dukesbox.presenters;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.scottdukes.dukesbox.AgentManager;
import net.scottdukes.dukesbox.IDukesBoxConfig;
import net.scottdukes.dukesbox.SyncProgress;
import net.scottdukes.dukesbox.api.ApiClient;
import net.scottdukes.dukesbox.api.SyncContext;
import net.scottdukes.dukesbox.events.HostUpdated;
import net.scottdukes.dukesbox.views.IMainView;
import net.scottdukes.dukesbox.views.IProgressView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Subscription;
import rx.util.functions.Action1;

public class MainPresenter {
    private final Logger mLogger = LoggerFactory.getLogger(MainPresenter.class);
    private final Action1<Throwable> onSyncError = new Action1<Throwable>() {
        @Override
        public void call(Throwable t) {
            mLogger.error("Error getting sync context", t);
            stopClicked();
        }
    };
    private final Action1<SyncContext> onNextSyncContext = new Action1<SyncContext>() {
        @Override
        public void call(SyncContext context) {
            mAgentManager.update(context);
        }
    };
    private final EventBus mBus;
    private AgentManager mAgentManager;
    private ApiClient mApiClient;
    private IDukesBoxConfig mConfig;

    private IMainView mView;

    private Subscription mSubscription;
    private Map<String, IProgressView> mProgress = new HashMap<String, IProgressView>();

    @Inject
    public MainPresenter(IDukesBoxConfig config, AgentManager agentManager, ApiClient apiClient, EventBus bus) {
        mConfig = config;
        mAgentManager = agentManager;
        mApiClient = apiClient;
        mBus = bus;
        mBus.register(this);
    }

    public void init(IMainView view) {
        mView = view;
        onHostUpdated(new HostUpdated(mConfig.getBaseUrl()));
    }

    public void startClicked() {
        mView.setStatusText("Syncing...");
        if (mSubscription == null) {
            mSubscription =
        /*Observable.timer(mConfig.getPollPeriodInSeconds(), TimeUnit.SECONDS)
          .flatMap(new Func1<Long, Observable<SyncContext>>()
          {
            @Override
            public Observable<SyncContext> call(Long aLong)
            {
              return mApiClient.getSyncContext();
            }
          })
          .retry()*/

                    mApiClient.getSyncContext()
                            //.subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(onNextSyncContext, onSyncError);
        }
    }

    public void stopClicked() {
        mAgentManager.stop();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }

        mView.setStatusText("Idle");
    }

    @Subscribe
    public void onHostUpdated(HostUpdated event) {
        mView.setHostText(event.getHostAddress());
    }

    @Subscribe
    public void onSyncProgress(SyncProgress event) {
        String tag = event.getTag();
        if (!mProgress.containsKey(tag)) {
            mProgress.put(tag, mView.addProgress(tag, event.total()));
        } else {
            IProgressView progressView = mProgress.get(tag);

            if (event.isComplete()) {
                mView.removeProgress(tag);
                mProgress.remove(tag);
            } else {
                progressView.setProgress(event.count());
            }
        }
    }


    public void quitClicked() {
        mView.exit();
    }

    public void updateConfigClicked() {
        mView.navigateToSettings();
    }

    public void dispose() {
        mAgentManager.stop();
        mBus.unregister(this);
    }
}
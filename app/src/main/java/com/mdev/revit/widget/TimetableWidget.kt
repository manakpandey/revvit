package com.mdev.revit.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.mdev.revit.R
import com.mdev.revit.ui.MainActivity
import org.jetbrains.anko.toast


class TimetableWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(context!!.packageName, TimetableWidget::class.java.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.timetable_widget)

            val intentUpdate = Intent(context, TimetableWidget::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val piUpdate = PendingIntent.getBroadcast(context, 2, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.update, piUpdate)


            views.setRemoteAdapter(R.id.widget_list_view, Intent(context, TimetableWidgetService::class.java))
            val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preference_schedule),Context.MODE_PRIVATE)

            val appIntent = Intent(context, MainActivity::class.java)
            val appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent)

            views.setEmptyView(R.id.widget_list_view, R.id.no_data)
            if (sharedPref.getBoolean("tt_set",false)) {
                views.setTextViewText(R.id.no_data_text, "No Classes Today")
                val pendingIntent = PendingIntent.getActivity(context, 1, appIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                views.setOnClickPendingIntent(R.id.no_data_text, pendingIntent)
            }
            else{
                views.setTextViewText(R.id.no_data_text, "Please login to FFCS to sync your timetable")
                val pendingIntent = PendingIntent.getActivity(context, 1, appIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                views.setOnClickPendingIntent(R.id.no_data_text, pendingIntent)
            }
            // Instruct the widget manager to update the widget
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            context.toast("Widget Updated")
        }
    }
}


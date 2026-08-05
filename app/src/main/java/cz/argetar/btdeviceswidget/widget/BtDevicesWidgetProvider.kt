package cz.argetar.btdeviceswidget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import cz.argetar.btdeviceswidget.R

class BtDevicesWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.bt_devices_widget)
            views.setTextViewText(R.id.widget_title, "BT devices")
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

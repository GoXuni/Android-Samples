package com.example.xuniweather;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.grapecity.xuni.core.*;
import com.grapecity.xuni.chartcore.*;
import com.grapecity.xuni.flexgrid.*;
import com.grapecity.xuni.flexchart.*;

public class MainActivity extends Activity {
	private ObservableList<WeatherData> wd;
	private FlexChart chart;
	private FlexGrid grid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.grapecity.xuni.core.LicenseManager.KEY = License.KEY;
        chart = (FlexChart)findViewById(R.id.flexchart);
        grid = (FlexGrid)findViewById(R.id.flexgrid);
                
        grid.setItemsSource(wd);
        chart.setItemsSource(wd);

        grid.setVisibility(View.GONE);
        chart.setVisibility(View.GONE);
        
        final EditText editText = (EditText)findViewById(R.id.zip);
     
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.ACTION_DOWN) {
                	WeatherTask task = new WeatherTask();
                	String zip = editText.getText().toString();
                	task.execute(new String[]{zip});
                	try {
                			wd = task.get();
						chart.getSeries().clear();
				        chart.setBindingX("date");
				     
				        ChartSeries seriesHumidity = new ChartSeries(chart, "Humidity %", "humidity");
				        ChartSeries seriesHighTemp = new ChartSeries(chart, "High Temp (F)", "highTemp");
				        seriesHighTemp.setChartType(ChartType.LINE);
				        
				        chart.getSeries().add(seriesHumidity);
				        chart.getSeries().add(seriesHighTemp);
				    
				        chart.getAxisX().setLabelsVisible(true);
				        chart.getAxisY().setLabelsVisible(true);
				        
				        grid.setReadOnly(true);
				        grid.setAutoGenerateColumns(true); 
				        
				        grid.setItemsSource(wd);
				        chart.setItemsSource(wd);
				        
				        grid.setAlternatingRowBackgroundColor(Color.rgb(224, 224, 224));
				        seriesHumidity.setChartType(ChartType.AREA);
				        seriesHumidity.setColor(Color.rgb(189, 217, 242));
				        seriesHighTemp.setBorderWidth(2.5f);
				        chart.legend.setOrientation(LegendOrientation.AUTO);
				        chart.legend.setPosition(ChartPositionType.BOTTOM);			      
				        GridColumn dateCol = grid.getColumns().getColumn("date");
				        dateCol.setFormat("M-dd h:mm a");
				        dateCol.setName("date/time");
				        chart.getAxisX().setFormat("M-dd");
				        grid.setGridLinesVisibility(GridLinesVisibility.COLUMN);
				        grid.autoSizeColumns(0, grid.getColumns().size() - 1);
				        grid.refresh(false);
						chart.refreshChart();
						grid.setVisibility(View.VISIBLE);
						chart.setVisibility(View.VISIBLE);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    //return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
    
    private class WeatherTask extends AsyncTask<String,Void,ObservableList<WeatherData> >{

		@Override
		protected ObservableList<WeatherData> doInBackground(String... params) {
			// TODO Auto-generated method stub
	        WeatherData wd = new WeatherData();
	        ObservableList<WeatherData> list = wd.getList(params[0]);
			return list;
		}
    	
    }
}


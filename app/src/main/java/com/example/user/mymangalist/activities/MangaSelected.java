package com.example.user.mymangalist.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mymangalist.R;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;



public class MangaSelected extends ActionBarActivity
{
    public static String TAG="TAG";
    public String manganame;
    public String mangalink,mangalink1;
    getMangainfoTask task;
    getBitmapTask bitmapTask;
    String s;
    ArrayList<ArrayList<String>> taskresults;
    ListView descp;
    ImageView imageView;
    ArrayAdapter<String> adapter;
    Bitmap bm=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_selected);
        if (savedInstanceState != null)
        {
            Log.d(TAG, "Saved Instance State - chapterlinks - " + savedInstanceState.get("chapterlinks"));
            if (savedInstanceState.get("chapterlinks") != null)
            {
                Bundle bundle = getIntent().getExtras();
                manganame = bundle.getString("manganame");
                mangalink = bundle.getString("mangalink");
                TextView textView = (TextView) findViewById(R.id.manganame);
                textView.setText(manganame.toUpperCase());
                imageView = (ImageView) findViewById(R.id.mangaimage);
                Bitmap bitmap = (Bitmap) savedInstanceState.get("image");
                imageView.setImageBitmap(bitmap);
                descp = (ListView) findViewById(R.id.mangadescription);
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,Arrays.asList(savedInstanceState.getStringArray("descp")))
                {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent)
                    {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        textView.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                descp.setAdapter(adapter);
                ArrayList<String> chapterlinks = new ArrayList<>();
                chapterlinks = savedInstanceState.getStringArrayList("chapterlinks");
                Button download = (Button) findViewById(R.id.downloadbutton);
                final ArrayList<String> finalChapterlinks = chapterlinks;
                download.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(getApplicationContext(), Chapters.class);
                        intent.putExtra("chapterslinks", finalChapterlinks);
                        intent.putExtra("manganame", manganame);
                        startActivity(intent);
                    }
                });
            }
            else
            {
                Bundle bundle = getIntent().getExtras();
                manganame = bundle.getString("manganame");
                mangalink = bundle.getString("mangalink");
                mangalink1 = "http://www.mangareader.net" + bundle.getString("mangalink");
                Log.d(TAG, mangalink1);
                TextView textView = (TextView) findViewById(R.id.manganame);
                textView.setText(manganame.toUpperCase());
                task = new getMangainfoTask();
                try
                {
                    task.execute();
                    Toast.makeText(getApplicationContext(), taskresults.toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e)
                {
                }
                imageView = (ImageView) findViewById(R.id.mangaimage);
                bitmapTask = new getBitmapTask();
                Bitmap bm = null;
                descp = (ListView) findViewById(R.id.mangadescription);
                Button download = (Button) findViewById(R.id.downloadbutton);
                download.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(getApplicationContext(), Chapters.class);
                        intent.putExtra("chapterslinks", taskresults.get(2));
                        intent.putExtra("manganame", manganame);
                        startActivity(intent);
                    }
                });
            }
        }
        else
        {
            Bundle bundle = getIntent().getExtras();
            manganame = bundle.getString("manganame");
            mangalink = bundle.getString("mangalink");
            mangalink1 = "http://www.mangareader.net" + bundle.getString("mangalink");
            Log.d(TAG, mangalink1);
            TextView textView = (TextView) findViewById(R.id.manganame);
            textView.setText(manganame.toUpperCase());
            task = new getMangainfoTask();
            try
            {
                task.execute();
                Toast.makeText(getApplicationContext(), taskresults.toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception e)
            {
            }
            imageView = (ImageView) findViewById(R.id.mangaimage);
            bitmapTask = new getBitmapTask();
            Bitmap bm = null;
            descp = (ListView) findViewById(R.id.mangadescription);
            Button download = (Button) findViewById(R.id.downloadbutton);
            download.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(getApplicationContext(), Chapters.class);
                    intent.putExtra("chapterslinks", taskresults.get(2));
                    intent.putExtra("manganame", manganame);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if(taskresults!=null)
        {
            outState.putStringArrayList("chapterlinks", taskresults.get(2));
            outState.putStringArrayList("descp", new ArrayList<String>(Arrays.asList(taskresults.get(1).get(0))));
            outState.putParcelable("image", bm);
        }

    }

    public class getBitmapTask extends AsyncTask<Void,Void,Bitmap>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Log.d(TAG,"On PreexecuteBitmap");
            pd = ProgressDialog.show(MangaSelected.this,"","Loading",true);
        }
        @Override
        protected Bitmap doInBackground(Void... voids)
        {
            InputStream in = null;
            try
            {
                in = new URL(s).openStream();
                bm = BitmapFactory.decodeStream(in);

            } catch (IOException e)
            {

            }
            return  bm;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            imageView.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
            pd.dismiss();
        }
    }
    public class getMangainfoTask extends AsyncTask<Void,Void,ArrayList<ArrayList<String>>>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Log.d(TAG,"On Pre=execute");
            pd = ProgressDialog.show(MangaSelected.this,"","Loading",true);
        }
        @Override
        protected ArrayList<ArrayList<String>> doInBackground(Void... voids)
        {
            ArrayList<ArrayList<String>> newarraylist=new ArrayList<>();
            try
            {
                URL url = new URL(mangalink1);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                InputStream inputStream = httpURLConnection.getInputStream();
                Log.d(TAG,mangalink1);
                Log.d(TAG,inputStream.toString());
                newarraylist = parseforpopular(inputStream);
            } catch (Exception e)
            {

            }
            return newarraylist;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> arrayLists)
        {
            Log.d(TAG,"Onpostexecute");
            Log.d(TAG,arrayLists.toString());
            taskresults = arrayLists;
            s=taskresults.get(0).get(0);
            if(!taskresults.get(1).isEmpty())
            {
                adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,new ArrayList<String>(Arrays.asList(taskresults.get(1).get(0))))
            {
                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView)view.findViewById(android.R.id.text1);
                    textView.setTextColor(Color.BLACK);
                    return view;
                }
            };
            }
            if(!taskresults.get(1).isEmpty())
            descp.setAdapter(adapter);
            pd.dismiss();
            bitmapTask.execute();
            super.onPostExecute(arrayLists);
        }
    }
    public ArrayList<ArrayList<String>> parseforpopular(InputStream inputStream)
    {
        ArrayList<ArrayList<String>> finalresult = new ArrayList<>();
        ArrayList<String> results=new ArrayList<>(),manganames=new ArrayList<>(),mangaimages=new ArrayList<>(),mangadescp=new ArrayList<>(),mangachapterslinks=new ArrayList<>(),mangachapterslinks2=new ArrayList<>();
        OutputStream stream= new OutputStream()
        {
            @Override
            public void write(int i) throws IOException
            {

            }
        };
        //if(Character.isDigit(mangalink.charAt(1)))
        {
            int t=0;

            for(int a=1;a<mangalink.length();a++)
            {
                if(mangalink.charAt(a)=='/')
                    t=a;
            }
            if(t==0)
                t=mangalink.length()-1;
            Log.d(TAG,t+"");
            Document document = null;
            Tidy tidy = new Tidy();
            tidy.setTrimEmptyElements(false);
            tidy.setXHTML(true);
            document = tidy.parseDOM(inputStream, stream);
            Document document1 = (org.w3c.dom.Document) document;
            org.w3c.dom.Element root = (org.w3c.dom.Element) document1.getDocumentElement();
            NodeList nodeList = root.getElementsByTagName("img");
            Node node;
            NamedNodeMap namedNodeMap;
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Log.d(TAG, i + "");
                namedNodeMap = nodeList.item(i).getAttributes();
                Log.d(TAG,namedNodeMap.getLength()+"");
                for(int j=0;j<namedNodeMap.getLength();j++)
                {
                    if(namedNodeMap.item(j).getNodeName().equals("src"))
                        mangaimages.add(namedNodeMap.item(j).getNodeValue());
                }
            }
            finalresult.add(mangaimages);
            NodeList nodeList2 = root.getElementsByTagName("p");
            if(nodeList2.item(0).hasChildNodes())
            mangadescp.add(nodeList2.item(0).getFirstChild().getNodeValue());
            finalresult.add(mangadescp);
            NodeList nodeList3 = root.getElementsByTagName("a");
            NamedNodeMap namedNodeMap2;
            for(int k=0;k<nodeList3.getLength();k++)
            {
                namedNodeMap2 = nodeList3.item(k).getAttributes();
                for(int l=0;l<namedNodeMap2.getLength();l++)
                {
                    if(namedNodeMap2.item(l).getNodeName().equals("href"))
                    {
                       mangachapterslinks.add(namedNodeMap2.item(l).getNodeValue());

                    }

                }
                if(mangachapterslinks.get(k).startsWith(mangalink))
                    mangachapterslinks2.add(mangachapterslinks.get(k));
            }
            for(int i=0;i<6;i++)
            mangachapterslinks2.remove(0);
            finalresult.add(mangachapterslinks2);
            Log.d(TAG,mangachapterslinks2.size()+"");
            Log.d(TAG,mangachapterslinks2.toString());
        }
        return finalresult;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manga_selected, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

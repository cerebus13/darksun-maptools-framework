package tokenmaker2;

import java.io.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 * CompendiumEntry models an entry in the Wizards online compendium. It is a
 * base class dealing with all the general compendium requirements and is
 * subclassed off into {@link NPCCompendiumEntry} and
 * {@link PowerCompendiumEntry} which deal with Monster and Power entries
 * respectively.
 * 
 * @author Blakey, Summer 2010.
 */
public class CompendiumEntry
{

    protected String            myHTML;
    protected String            myURL;
    private String              actionUrl;
    private static final String baseURL_local  = "http://localhost/ddi/";
    private static final String baseURL_remote = "http://www.wizards.com/dndinsider/compendium/";
    private String              email;
    private String              password;
    private NameValuePair[]     myPostData     = null;

    /**
     * Constructor for CompendiumEntry. This takes a URL, in String form, and
     * builds a CompendiumEntry from it. It deals with logging into the
     * compendium if required.
     * 
     * @param stringUrl
     *            A String representation of the full path of the compendium
     *            page we want to build
     */
    public CompendiumEntry(String stringUrl)
    {
        myURL = stringUrl;
        init();
    }

    /**
     * init does all the work of building the CompendiumEntry
     */
    private void init()
    {
        try
        {
            HttpClient client = new HttpClient();
            // support a proxy if it is set up
            String proxyHost = System.getProperty("http.proxyHost");
            if (proxyHost != null)
            {
                int proxyPort = Integer.parseInt(System
                        .getProperty("http.proxyPort"));
                HostConfiguration config = client.getHostConfiguration();
                config.setProxy(proxyHost, proxyPort);
            }

            // Perform a GET on the page we were given
            GetMethod get = new GetMethod(myURL);
            client.executeMethod(get);
            myHTML = get.getResponseBodyAsString();
            get.releaseConnection();

            // figure out where we have to go to post the response - look up
            // the 'action=' tag
            parseAction(myHTML);
            // if our actionUrl contains login.aspx
            // then we need to login - so post the data back to the form
            if (actionUrl.contains("login.aspx"))
            {
                if (myPostData == null)
                {
                    // go and read DDi login details from file
                    readLoginDetails(TokenMaker.ddiFile);

                    // Now go and set up the post data
                    myPostData = new NameValuePair[5];
                    myPostData[0] = new NameValuePair("email", email);
                    myPostData[1] = new NameValuePair("password", password);
                    // do the other fields in postData
                    parseFormData(myHTML);
                }

                // POST back with login details included.
                PostMethod post = new PostMethod(actionUrl);
                post.setRequestBody(myPostData);
                client.executeMethod(post);
                post.releaseConnection();
                // we're now logged in so just read the URL
                get = new GetMethod(myURL);
                client.executeMethod(get);
                myHTML = get.getResponseBodyAsString();
                get.releaseConnection();
            }
            formatHTML();

        }
        catch (Exception e)
        {
            System.err.println("Error initializing a CompendiumEntry: " + e);
            System.err.println(e.getStackTrace());
        }
    }

    /**
     * Take myHTMLSource and add in the formatting strings to pick up the same
     * formating that Wizards use.
     * <p>
     * I've had to use this approach because java doesn't support CSS very well,
     * so I've effectively added the CSS inline to the HTML direct.
     */

    protected void formatHTML()
    {
        // ditch the useless stuff around the outside
        int start = myHTML.indexOf("<div id=\"detail\">");
        int end = myHTML.indexOf("</div>");
        myHTML = myHTML.substring(start, end + 6);

        // add some newlines to the source for readability for me!
        myHTML = myHTML.replace("<p", "\n\n<p");
        myHTML = myHTML.replace("<br/>", "<br/>\n\n");
    }

    /**
     * Return the HTML source code we have for this CompendiumEntry.
     * 
     * @return the source.
     */
    public String getHTML()
    {
        return myHTML;
    }

    /**
     * Get back the URL that this CompendiumEntry is modelling.
     * 
     * @return the URL
     */
    public String getURL()
    {
        return myURL;
    }

    /**
     * Go get the users DDi account info from a file. This file is assumed to
     * contain just 2 lines.
     * <p>
     * The first is the user's email address. <br/>
     * The second is the user's password. <br/>
     * Both of these will later be used to login to the DDi Compendium.
     * 
     * @param filename
     */
    private void readLoginDetails(String filename)
    {
        try
        {
            File file = new File(filename);
            FileReader r = new FileReader(file);
            BufferedReader reader = new BufferedReader(r);
            email = reader.readLine();
            password = reader.readLine();
            reader.close();

        }
        catch (Exception e)
        {
            System.err.println("Error opening login details file: " + e);
        }

    }

    /**
     * Take a string which contains the source of an HTML file and parse it for
     * the <input> tags that need to be used to perform a POST on a page.
     * 
     * @param source
     *            - the HTML source we will parse
     * 
     * @TODO - This code should probably be reworked to use RegExps.
     */
    private void parseFormData(String source)
    {
        try
        {
            int i = 2; // index for 'email' into myPostData - array where we are
            // holding the data we are posting
            while (source.contains("<input"))
            {
                // First grep out the <input> tag.
                int start = source.indexOf("<input");
                String input = source.substring(start);
                int end = input.indexOf('>');
                input = input.substring(0, end + 1);
                source = source.substring(start + end);

                // Now grep the type= attribute from the input tag
                if ((start = input.indexOf("type=")) != -1)
                {
                    String type = input.substring(start + 6);
                    end = type.indexOf("\"");
                    type = type.substring(0, end);
                    if (type.equals("text") || type.equals("password"))
                        continue; // we can't deal with text fields here.
                }

                // Now grep the name= attribute from the input tag
                start = input.indexOf("name=");
                String name = input.substring(start + 6);
                end = name.indexOf("\"");
                name = name.substring(0, end);
                // System.out.println("Name: " +name);

                // Now grep the value= attribute from the input tag
                start = input.indexOf("value=");
                String value = input.substring(start + 7);
                end = value.indexOf("\"");
                value = value.substring(0, end);
                // System.out.println("Value: " +value);
                myPostData[i++] = new NameValuePair(name, value);
            }
        }
        catch (Exception e)
        {
            System.err
                    .println("Error parsing login source for form data: " + e);
        }
    }

    /**
     * Take a string which contains the source of an HTML file and parse it for
     * the action="file" tag.
     * 
     * @param source
     *            - the HTML source we will parse
     * 
     * @TODO - This code should probably be reworked to use RegExps.
     */
    private void parseAction(String source)
    {
        int start = source.indexOf("action=");
        String action = source.substring(start + 8);
        int end = action.indexOf("\"");
        actionUrl = ((TokenMaker.isRemote) ? baseURL_remote : baseURL_local)
                + action.substring(0, end);
    }
}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Instance of the XMLParser class
        XMLParser xmlParser = new XMLParser();
        xmlParser.loadDocument();
        xmlParser.saveUpdatedXMLToBackup();
        xmlParser.updateElement();

    }
}

/*
I have shed tears in this task sir.
There was a moment where adding the new element under db attributes iterated itself.
That was the most painful endeavour.
Irregardless of my predicament, hope I have delivered as per what was required.
The code's logic was just to read the xml values, add a new element under db, update elements as per the user's demands.
Finally, to create a config_backup.
Still don't know the intended use of the XML config file when it comes to APIs.
 */
Hi! Thank you for your interest in contributing :-)

# If you found a bug
* please open a issue
  * it helps if you tell me what android version you are using
  * it helps if you tell me which version of the app you are using (the app itself does not show it but in fdroid you should see the installed and available version)
  
# If you want to add or correct a translation

## If you're familiar with git and github
* Please try to create a pull request, this makes it easy for me to integrate your changes
  * the translations are located in app/src/main/res (https://github.com/tobihille/SeniorLauncher/tree/master/app/src/main/res)
  * if you are adding a translation
    * add a folder called "values-[2-letter-language-code]-r[2-letter-territory-code-in-uppercase]", for example values-fr-rFR
    * inside that folder add a file called "strings.xml" (you can use any of the other strings.xml - files as a template. The english one is located in https://github.com/tobihille/SeniorLauncher/blob/master/app/src/main/res/values/strings.xml
* If you need help with pullrequests: https://help.github.com/articles/creating-a-pull-request/    
    
## If you are not familiar with git or github
  * please open a issue, set the title to "new translation [2-letter-language-code] [2-letter-territory-code-in-uppercase]"
  * insert the translations for the following words (please next to the original strings because I have to copy-and-paste it and I probably don't understand any of your inserted words ;-) ):
    * ``Call``
    * ``Images``
    * ``Show all images``
    * ``Go back``
    * the emergency number for the country, e.g. 112 or 911 (this is important!)
    * ``Settings`` (currently not used)
    * ``Loading, please wait a moment``
  * If the language uses special characters please try to encode them like they would be on a web page (e.g. the german ``ä`` would become a ``&auml;``)
  
# Code of conduct
* be excellent to each other

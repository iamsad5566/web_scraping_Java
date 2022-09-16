Crawler for NTU BAC lab
===
## Java version: 16
This is an automated crawler for BAC lab. 
The crawler will crawl data from the NTU accounting web, after sorting the data, an Xls file will be exported to the /output folder.

Note
---
* Load the config.yml (From local, pleas put it under the root path). <b>config.yml must existed</b>.
* Take care of the format of yaml file, all the symbols are meaningful (include the space). See [yaml doc](https://www.cloudbees.com/blog/yaml-tutorial-everything-you-need-get-started) for detail.
* Put the `chromedriver / chromedriver.exe` under the root path, here [download](https://chromedriver.chromium.org/) the chromedriver.
* If you use Windows system, change the OS: mac into OS: windows in the `config.yml`. If you use macOS system, make sure you have allowed the chromedriver driven by the system (Open the privilege in the security and privacy).
 
> OS: mac  ->  OS: windows 

How to use?
---
1. Package the file with Maven and run the output `.jar` file. The file would be put in `/target` folder
2. Directly run this program through the `main` method under the `Crawler` class.  
> / src / main / java / Crawler.java  
3. Convert the `.jar` file to `.exe`, open it by simply double clicks. Visited [Launch4J](http://launch4j.sourceforge.net/) to get more details.

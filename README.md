Crawler for NTU BAC lab
===
This is an automated crawler for BAC lab.

Note
---
* Load the config.yml (From local, pleas put it under the root path). <b>config.yml must existed</b>.
* Take care of the format of yaml file, all the symbols are meaningful (include the space). See [yaml doc](https://www.cloudbees.com/blog/yaml-tutorial-everything-you-need-get-started) for detail.
* Put the chromedriver / chromedriver.exe under the root path, here [download](https://chromedriver.chromium.org/) the chromedriver.
* If you use Windows system, change the OS: mac into OS: windows in the config.yml. If you use macOS system, make sure you have allowed the chromedriver driven by the system (Open the privilege in the security and privacy).
  > OS: mac  ->  OS: windows 

How to use?
---
1. Package the file with Maven and run the output .jar file.
2. Directly run this program through the `main` method under the `Crawler` class.
3. Convert the .jar file to .exe, open it by simply double clicks.
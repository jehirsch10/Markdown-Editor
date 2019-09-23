# Markdown Editor

An open source markdown editor written in `JavaFX`. 

![Markdown Editor screenshot](image/screenshot.png)

## Features
- Create or edit markdown and html files
- Html preview
- Style selection (**CSS**)
- **Pdf** and **Html** export
- Syntax highlighting


### Dependencies
This project is using [RichTextFX](https://github.com/FXMisc/RichTextFX)

Based on [Github flavored markdown](https://github.github.com/gfm/)

### Architecture
Markdown Editor is made with [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) pattern. It separate clearly JavaFX view and Java back-end, allowing you to use the Model for view made with another technology (for example Swing or else).

## Download
[JAR and EXE available here](https://github.com/kiidness/Markdown-Editor/releases/download/1.0.0/markdown-editor-1.0.0.zip)

## Set-Up (NEW) 
1. install a recent java(10+)
2. install a recent javafx(11)
	* create user library with the javafx jars
	* add this library to classpath
	* add below to vm arguments in the run config
		--module-path /usr/lib/jvm/javafx-sdk-11.0.2/lib --add-modules=javafx.controls,javafx.fxml,javafx.web
3. clone repo
4. add junit to classpath
5. download the following
	* richtextfx fat jar from [RichTextFX](https://github.com/FXMisc/RichTextFX) and add it to the classpath
	* html2pdf fat jar from [itext-pdfhtml](https://github.com/itext/i7j-pdfhtml) and add it to the classpath
	* kernel jar from [itext](https://github.com/itext/itext7) and add it to the classpath

## Releases
- [1.1.8-beta](https://github.com/kiidness/Markdown-Editor/releases/tag/1.1.8-beta)
- [1.0.0-beta](https://github.com/kiidness/Markdown-Editor/releases/tag/1.0.0)

## License
Licensed under [MIT License](https://github.com/kiidness/Markdown-Editor/blob/master/LICENSE).

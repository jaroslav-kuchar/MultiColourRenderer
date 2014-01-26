# Multi Colour Renderer 

Author: Jiri Krizek (original author: Feretti from <a href="http://forum.gephi.org/">Gephi forum</a>)

Supervisor: Jaroslav Kuchar

Multi Colour Renderer is plugin for <a href="http://www.gephi.org">Gephi</a>. 
This plugin allows rendering of nodes in "Preview" mode as a "multicolour pie". Each node is rendered using multiple colours.
The plugin uses an implementation proposed by the user Ferretti published on Gephi forum (http://forum.gephi.org/viewtopic.php?f=30&t=1665)

Code taken from forum and packaged as plugin.
Supports only visualization in the Processing window, visualization export to PDF or SVG is not supported.

Limitations:

* Only Processing window visualization is supported, export to PDF or SVG currently not supported.

Inputs:

* "colourList" attribute as String
* the comma delimited list of integers representing rgb of colours

![rend](https://raw.github.com/jaroslav-kuchar/MultiColourRenderer/master/images/renderer.png)

This plugin is used as visualization support for other plugins:
* <a href="https://github.com/jaroslav-kuchar/MCodeClustering">MCODE Clustering</a>
* <a href="https://github.com/jaroslav-kuchar/GirmanNewmanClustering">Girvan Newman Clustering</a>
* <a href="https://github.com/jaroslav-kuchar/MarkovClustering">Markov Clustering</a>


## License
The GPL version 3, http://www.gnu.org/licenses/gpl-3.0.txt

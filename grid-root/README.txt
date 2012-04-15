grid README
=================================================

PREREQUISITES

  - Java 5
  - Maven 2.2
  - A working internet connection

CHECKING OUT THE CODE

  The source code is maintained in grid Subversion repository. To check
  out the latest development version, use the following command:

    $ svn co http://example.com/svn/.../trunk grid

EDITING THE CODE IN AN IDE

  The grid add-on project can be imported in any IDE that supports Maven.

TRYING OUT THE DEMO

  1. Compile and install the entire project:

    $ mvn install

  2. Start the built-in Jetty web server:

    $ cd demo
    $ mvn jetty:run

  3. Open your favorite web browser and point it to:

    http://localhost:8080/demo/

READING THE MANUAL

  1. Generate the manual:

    $ cd manual
    $ mvn docbkx:generate-html

  2. Open the file manual/target/docbkx/html/manual.html
     in your favorite web browser.

PUBLISHING THE ADD-ON

  1. Build the add-on:

    $ mvn package assembly:assembly

  2. Publish the add-on to the Vaadin directory (http://vaadin.com/directory)

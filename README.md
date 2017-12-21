# Ämnesspecifika handböcker (reference guides)

This is a portlet application which presents published document lists and allows access to the documents. It is integrated 
with [iFeed](https://github.com/Vastra-Gotalandsregionen/oppna-program-ifeed) as the document lists are fetched from
iFeed. IFeed delivers metadata, e.g. the URL to the document. The client may therefore request the application to
present the document on that URL.

The application may be used without logging in. Only if any admin functionality is to be used, or if the application is
accessed from outside the firewall, login is required.

In the main view the user can choose a chapter to the left:

![](https://github.com/Vastra-Gotalandsregionen/handbok/raw/master/doc/1.png)

The user may then choose a document in the list after which the document is presented inline:

![](https://github.com/Vastra-Gotalandsregionen/handbok/raw/master/doc/2.png)

A user who has been granted permission to manage the chapter list may click the "Administrera" link in the upper right:

![](https://github.com/Vastra-Gotalandsregionen/handbok/raw/master/doc/4.png)

The chapters are added toghether with its Ifeed ID.

The portal administrator also has permission to access the portlet settings:

![](https://github.com/Vastra-Gotalandsregionen/handbok/raw/master/doc/3.png)

![](https://github.com/Vastra-Gotalandsregionen/handbok/raw/master/doc/5.png)

The top section contains settings which are global and thus not related to a specific portlet instance. There the
available reference guides are configured.

From the available reference guides, configured in the top section, a choice of which reference guide this
specific portlet instance uses is made.

At the bottom section the users' VGR ID is given in order to give them access to the "Administrera" link and view.

The project is part of the Region of Västra Götaland's commitment to open source software within
[Öppna Program](http://vastra-gotalandsregionen.github.io/oppna-program/). 

## Software

The application is a portlet with a small layer of <i>Spring Portlet MVC</i> and <i>Angular</i> as front-end technology.
The front-end communicates with a Spring Rest back-end. The rest back-end is stateless but is deployed in the portlet
war file. The persistence is kept in a PostgreSQL database.

The major software components/frameworks are listed below:

* Liferay Portal 6.2
* Spring Portlet MVC 4.3.6
* Spring Framework 4.3.6
* Hibernate
* Angular
* PostgreSQL 9.5

## Authentication / authorization

This is kind of a special case regarding authentication. The portal uses cookie based authentication while the rest
web service preferably utilizes something stateless like JWT. But since the application is run in a portal the portal is
the part who owns the authentication.

The solution used here is that the portlet backend issues a JWT, which only is done if the user is logged in, which the
client can use in order to communicate with the rest layer. Note that only write operations to the rest layer requires
authentication and authorization.

An issue is that the Angular application has no way to detect when the user logs out of the portal. So to remedy this
issue the JWT is issued for short periods and renewed whenever needed.

## Security considerations

The documents are made available through the back-end through proxying. So the documents are available to the client
independently of whether the documents are avialable directly from the source to the client. Some documents are only
accessible from within the firewalls, but as the application is accessible from outside the firewall also those
documents are accessible by means of proxying through the application.

The fetching of documents are made by the client who requests the server to deliver a document from a specified URL. It
is important that the client can't instruct the server to fetch arbitrary URL:s. To constrain the client from making the
server request arbitrary resources the URL:s delivered to the client are paired with a HMAC. The HMAC must be delivered
to the server and must be verified in order for the server to deliver the document. So only authenticated requests will
be fulfilled.

## Getting started

Make sure the following is installed:

* Java >= 8
* Maven

Clone the Git repository and run `mvn package` and deploy `core-bc/modules/portlet/target/handbok.war` to the deploy
directory of Liferay. Building the application has no prior dependency of Node or NPM; they are downloaded during the
Maven build.

To build with ahead-of-time (AOT) and minification compilation, run `mvn package -Pprod`.


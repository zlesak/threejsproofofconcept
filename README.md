# MISH APP - front end application part

![MISH_LOGO](src/main/webapp/icons/MISH_icon.ico "MISH APP Logo")

## Description

This repository contains the frontend part of the MISH APP application that has been created as a part of a master thesis at the University of Hradec Kralove.  
Provides a web-based user interface for interaction with anatomical 3D models.
Code in this repository is based on Vaadin framework for the UI part and Three.js for 3D rendering.  
The backend part is located in a separate repository: https://github.com/Foglas/mishprototype.

## Running the application

To run the application with back end in action, there has been made a separate repository MISH SCRIPTS making it easy to launch both front end and back end.  
More information can be found in the MISH SCRIPTS repository.  
Link to MISH SCRIPTS repository: https://github.com/zlesak/MISH_SCRIPTS

## Project structure

Project has been divided into several packages and directories:  
- `src.main.java`
  - `cz.uhk.zlesak.threejslearningapp.application.clients` - contains client classes for communication via API of called services
  - `cz.uhk.zlesak.threejslearningapp.application.components` - contains reusable UI components and component compositions
  - `cz.uhk.zlesak.threejslearningapp.application.controllers` - contains controller classes for handling user interactions on views
  - `cz.uhk.zlesak.threejslearningapp.application.events` - contains event classes for event handling
  - `cz.uhk.zlesak.threejslearningapp.application.exceptions` - contains custom exception classes
  - `cz.uhk.zlesak.threejslearningapp.application.files` - contains classes related to file handling
  - `cz.uhk.zlesak.threejslearningapp.application.i18n` - contains internationalization (i18n) related classes
  - `cz.uhk.zlesak.threejslearningapp.application.models` - contains data models used in the application
  - `cz.uhk.zlesak.threejslearningapp.application.utils` - contains utility classes
  - `cz.uhk.zlesak.threejslearningapp.application.views` - contains all views (pages) of the application
  - `cz.uhk.zlesak.threejslearningapp.security` - contains security configuration and classes
- `src.main.frontend`
  - `js`- contains Three.JS renderer, editor.js and related files
  - `themes`- contains styles and themes for the application
  - `types`- contains TypeScript type definitions for JavaScript libraries used in the project
- `src.main.resources` - contains resources used by application in running context
- `src.main.webapp` - contains web application files like icons or static images


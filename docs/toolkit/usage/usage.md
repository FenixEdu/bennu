## [**Usage**](./usage.md)
Bennu Toolkit comes bundled with Bennu Portal, so if your application is depending from this module (and most are) you have it available.

## Using JSP
Each page that wishes to use this library must add the following line at the top of your JSP code:

```
${portal.toolkit()}
```

If you are using Angular the following code will import both angular.js and the toolkit version for angular:

```
${portal.angularToolkit()}
```

## Other technologies
you can add the toolkit just by importing the correct file:

```javascript
<link href="{contextPath}/bennu-toolkit/css/toolkit.css" rel="stylesheet"/>
<script type="text/javascript" src="{contextPath}/bennu-toolkit/js/toolkit.js"></script>
```

and if you are using angluar

```javascript
<script type="text/javascript" src="{contextPath}/bennu-portal/js/angular.min.js"></script>
<script type="text/javascript" src="{contextPath}/bennu-toolkit/js/toolkit-angular.js">
</script><link href="{contextPath}/bennu-toolkit/css/toolkit.css" rel="stylesheet"/>
```

Your Angular app must depend on the bennuToolkit module

```javascript
angular.module('yourApp', ['otherModule1', 'bennuTookit', 'otherModuleN']).config(...).run(...);
```
import json
raw = open('model.json.old').read()
obj = json.loads(raw)

for host in obj['hosts']:
	print("host: %s" % host['hostname']) 
	for app in host['apps']:
		appPath = app['functionalities'][0]['path']
		app['path'] = appPath
		print("\tapp: %s path: %s" % (app['title'], app['path']))
		for func in app['functionalities']:
			func['description'] = func['title']
f = open('model.json','w')
f.write(json.dumps(obj,  sort_keys=False,indent=4, separators=(',', ': ')))
f.close()

export const getRouteDefinition = (route, matched, routes) => {
  const level = matched.findIndex(r => r.name === route || r.path === route)
  let r
  for (let i = 0; i <= level; i++) {
    const matchedRoute = matched[i]
    r = routes.find(rd => rd.name === matchedRoute.name || rd.path === matchedRoute.path)
    routes = r.children
  }
  return r
}

/**
 * @param {import('vue-router').RouteRecord[]} matched
 */
export const getMeta = (route, name) => {
  const metas = route.matched.filter(r => r.meta[name]).map(r => r.meta[name])
  return metas.length > 0 ? metas[metas.length - 1] : undefined
}

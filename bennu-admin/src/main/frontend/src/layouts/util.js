/*
 * this file is placed inside @/layouts and not in @/utils for colocation,
 * i.e. files that are closely related should be near each other
 */
export function shouldResetPageFocus (to, from) {
  const isSameRoute = to.name === from.name || to.path === from.path
  const fromParentRoute = from.matched?.[from.matched.length - 1].parent
  const toParentRoute = to.matched[to.matched.length - 1].parent
  const isNavigationBetweenSiblings = fromParentRoute !== undefined &&
    toParentRoute !== undefined &&
    (fromParentRoute.name === toParentRoute.name || fromParentRoute.path === toParentRoute.path)

  return !isSameRoute && !isNavigationBetweenSiblings
}

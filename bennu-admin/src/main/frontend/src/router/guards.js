import store from '@/store'

/**
 * @callback GetErrorRoute
 * @param {Error} error
 * @returns {Parameters<Parameters<import('vue-router').NavigationGuard>[2]>[0]}
 */
/**
 * Wraps a navigation guard. The returned guard will catch axios errors during the
 * execution of the wrapped `guard`. If the error has a 404 status, it redirects
 * to `PageNotFoundPage`. If the error has a 403 status, it redirects to
 * `UnauthorizedPage`. If another error happens, calls `getErrorRoute` if it is
 * defined, otherwise aborts navigation altogether (throws the error).
 *
 * **NOTE:** If any of the functions you pass as parameters need to access the
 * `this` keyword, make sure that they are **not** arrow functions, as this wrapper
 * will not be able to bind its `this` context to your functions.
 *
 * @param {import('vue-router').NavigationGuard} guard A vue-router navigation guard
 * @param {GetErrorRoute} [getErrorRoute] A function that receives an Error and
 * returns the next route. If not defined, `next(err)` is called when there's
 * an error
 * @returns {import('vue-router').NavigationGuard} The wrapper navigation guard
 */
export const guardWithErrorHandling = (guard, getErrorRoute) => {
  // cannot be arrow function because of 'this' context
  return async function (to, from, next) {
    try {
      await guard.bind(this)(to, from, next)
    } catch (err) {
      if (err.response?.status === 404) {
        // https://github.com/vuejs/vue-router/issues/977#issuecomment-304498068
        return next({ ...to, name: 'PageNotFoundPage', params: [to.path], replace: true })
      } else if (err.response?.status === 403) {
        return next({ name: 'UnauthorizedPage' })
      } else if (getErrorRoute) {
        return next(getErrorRoute.bind(this)(err))
      } else {
        next(err)
      }
    } finally {
      store.dispatch('completeProgressBar')
    }
  }
}

export const buildLoginCallback = () => {
  const currentLocation = window.location.href
  if (currentLocation.includes('/login')) {
    const origin = window.location.origin
    const context = process.env.VUE_APP_CTX ?? ''
    return new URL(origin.concat(context).concat('/bennu-admin'))
  }
  return currentLocation
}

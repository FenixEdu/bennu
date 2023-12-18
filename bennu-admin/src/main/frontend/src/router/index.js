import Vue from 'vue'
import Router from 'vue-router'
import store from '@/store'
import RefreshRoute from '@/utils/refresh-route'

// Navigation guards
import { buildLoginCallback, guardWithErrorHandling } from './guards'
// import { guardWithErrorHandling, buildLoginCallback } from './guards'

// Standard pages
import PageNotFoundPage from '@/pages/PageNotFoundPage'
import UnauthorizedPage from '@/pages/UnauthorizedPage'
import ErrorPage from '@/pages/ErrorPage'

// Utils
// import { getPageAndSectionFromHash, scrollOffset as DynamicFormScrollOffset, scrollBehavior as DynamicFormScrollBehavior } from '@/components/dynamic-form/utils/navigation'
// import { getMeta } from '@/router/utils'
import * as BennuCoreAPI from '@/api/bennu-core'
// import * as BennuAdminAPI from '@/api/bennu-admin'

// Pages
const DomainBrowserPage = () => import('@/pages/DomainBrowserPage.vue')
const DomainObjectPage = () => import('@/pages/DomainObjectPage.vue')
const RelationSetPage = () => import('@/pages/RelationSetPage.vue')

Vue.use(Router)

// eslint-disable-next-line no-unused-vars
const ScrollBehavior = {
  savePosition ({ sameRoute = true, differentRoute = false }) {
    return function (to, from, savedPosition) {
      if (sameRoute && to.path === from.path) {
        return savedPosition
      }
      if (differentRoute && to.path !== from.path) {
        return savedPosition
      }
      return { x: 0, y: 0 }
    }
  },
  tabs () {
    return function (to, from, savedPosition) {
      const parentPathTo = to.fullPath.substring(0, to.fullPath.lastIndexOf('/'))
      const parentPathFrom = from.fullPath.substring(0, from.fullPath.lastIndexOf('/'))
      if (parentPathTo === parentPathFrom) {
        return savedPosition
      }
      return { x: 0, y: 0 }
    }
  }
}

const router = new Router({
  mode: 'history',
  base: `${process.env.CTX && !process.env.DEV ? '/' + process.env.CTX : ''}/fenixedu-bennu-admin`,
  routes: [
    {
      path: '/',
      name: 'LandingPage',
      redirect: { name: 'DomainBrowserPage' }
    },
    {
      path: '/domain-browser',
      name: 'DomainBrowserPage',
      component: DomainBrowserPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout'
      },
      props: (route) => ({
        query: route.query.q
      })
    },
    {
      path: '/domain-browser/:domainObjectId',
      name: 'DomainObjectPage',
      component: DomainObjectPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        async beforeLoad (to, from) {
          const domainObject = await BennuCoreAPI.getDomainObject({ objectId: to.params.domainObjectId })

          to.meta.domainObject = domainObject
        }
      },
      props: (route) => ({
        domainObject: route.meta.domainObject
      }),
      beforeEnter: guardWithErrorHandling(async (to, from, next) => {
        await to.meta.beforeLoad(to, from)
        next()
      })
    },
    {
      path: '/domain-browser/:domainObjectId/relation-set/:relationSetName',
      name: 'RelationSetPage',
      component: RelationSetPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout'
      },
      props: route => ({
        domainObject: route.meta.domainObject,
        relationSet: route.meta.relationSet
      }),
      beforeEnter: guardWithErrorHandling(async (to, from, next) => {
        const [domainObject, relationSet] = await Promise.all([
          BennuCoreAPI.getDomainObject({ objectId: to.params.domainObjectId }),
          BennuCoreAPI.getRelation({ objectId: to.params.domainObjectId, relationName: to.params.relationSetName })
        ])

        to.meta.domainObject = domainObject
        to.meta.relationSet = relationSet

        next()
      })
    },
    {
      path: '/login',
      name: 'LoginPage',
      meta: {
        public: true
      },
      beforeEnter (to, from, next) {
        const callback = buildLoginCallback()
        window.location.href = `${process.env.VUE_APP_CTX ?? ''}/fenixedu-connect?callback=${callback}`
      }
    },
    {
      path: '/error',
      name: 'ErrorPage',
      component: ErrorPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        public: true
      },
      props: (route) => ({ error: route.params.error }),
      beforeEnter: (to, from, next) => {
        if (!!to.params.error && to.params.error instanceof Error) {
          next()
        } else {
          next({ path: '/' })
        }
      }
    },
    {
      path: '/unauthorized',
      name: 'UnauthorizedPage',
      component: UnauthorizedPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        public: true
      },
      props: (route) => ({
        errorKey: route.params.key,
        errorMessage: route.params.message
      })
    },
    {
      path: '*',
      name: 'PageNotFoundPage',
      component: PageNotFoundPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        public: true
      }
    },
    {
      path: '/logout',
      name: 'Logout'
    }
  ],
  scrollBehavior (to, from, savedPosition) {
    if (to.meta.scrollBehavior instanceof Function) {
      return to.meta.scrollBehavior.call(this, to, from, savedPosition)
    }
    return savedPosition ?? { x: 0, y: 0 }
  }
})

Vue.use(RefreshRoute, { router })

router.beforeEach(async (to, from, next) => {
  // Clear non-persisted notifications
  if (store.state.topMessage.active && !store.state.topMessage.persist) {
    Vue.prototype.$notification.clear()
  }

  if (to.path === from.path) {
    next()
    return
  }

  await Vue.prototype.$progress.set(10)
  if (to.path === '/logout') {
    await router.app.$auth.logout()
    return
  }
  const isLogged = await router.app.$auth.isLogged()
  await Vue.prototype.$progress.set(60)
  if (isLogged || to.meta.public) {
    next()
  } else {
    next({ name: 'LoginPage' })
  }
})

router.afterEach(async (to, from) => {
  if (to.path !== from.path) {
    await Vue.prototype.$progress.complete()
  }
})

router.onError((error) => {
  Vue.config.errorHandler(error, router.app, null)
})

export default router

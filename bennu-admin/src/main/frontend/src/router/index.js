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
import * as BennuAdminAPI from '@/api/bennu-admin'

// Pages
const DomainBrowserPage = () => import('@/pages/DomainBrowserPage.vue')
const DomainObjectPage = () => import('@/pages/DomainObjectPage.vue')
const DomainObjectRoleSetPage = () => import('@/pages/DomainObjectRoleSetPage.vue')
const DomainObjectSlotsTab = () => import('@/pages/DomainObjectSlotsTab.vue')
const DomainObjectRolesTab = () => import('@/pages/DomainObjectRolesTab.vue')
const DomainObjectRoleSetsTab = () => import('@/pages/DomainObjectRoleSetsTab.vue')

Vue.use(Router)

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
      redirect: { name: 'DomainObjectSlotsTab' },
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        // this is currently not working (see)
        async beforeRouteLoad (to, from) {
          const [domainObject, metadata] = await Promise.all([
            BennuAdminAPI.getDomainObject({ objectId: to.params.domainObjectId }),
            BennuAdminAPI.getDomainObjectMeta({ objectId: to.params.domainObjectId })
          ])

          to.meta.domainObject = domainObject
          to.meta.domainObject.metadata = metadata
        }
      },
      props: (route) => ({
        domainObject: route.meta.domainObject
      }),
      beforeEnter: guardWithErrorHandling(async (to, from, next) => {
        await to.meta.beforeRouteLoad(to, from)
        next()
      }),
      children: [
        {
          path: 'slots',
          name: 'DomainObjectSlotsTab',
          component: DomainObjectSlotsTab,
          meta: {
            scrollBehavior: ScrollBehavior.tabs(),
            async beforeLoad (to, from) {
              to.meta.page = Number(to.query.page) || 1
              to.meta.perPage = 10

              const domainObjectSlots = await BennuAdminAPI.listDomainObjectSlots({
                objectId: to.params.domainObjectId,
                query: to.query.q,
                page: to.meta.page,
                perPage: to.meta.perPage
              })

              to.meta.totalItems = domainObjectSlots.totalItems
              to.meta.domainObjectSlots = domainObjectSlots.items
            }
          },
          props: (route) => ({
            query: route.query.q,
            page: route.meta.page,
            perPage: route.meta.perPage,
            totalItems: route.meta.totalItems,
            domainObject: route.meta.domainObject,
            domainObjectSlots: route.meta.domainObjectSlots
          }),
          beforeEnter: guardWithErrorHandling(async (to, from, next) => {
            await to.meta.beforeLoad(to, from)
            next()
          })
        },
        {
          path: 'roles',
          name: 'DomainObjectRolesTab',
          component: DomainObjectRolesTab,
          meta: {
            scrollBehavior: ScrollBehavior.tabs(),
            async beforeLoad (to, from) {
              to.meta.page = Number(to.query.page) || 1
              to.meta.perPage = 10

              const domainObjectRoles = await BennuAdminAPI.listDomainObjectRoles({
                objectId: to.params.domainObjectId,
                query: to.query.q,
                page: to.meta.page,
                perPage: to.meta.perPage
              })

              // Load slots
              await Promise.all(domainObjectRoles.items.map(async (role) => {
                if (role.objectId) {
                  const slots = await BennuAdminAPI.listDomainObjectSlots({
                    objectId: role.objectId,
                    query: to.query.q,
                    page: 1,
                    perPage: 5
                  })
                  role.slots = slots.items
                }
              }))

              to.meta.totalItems = domainObjectRoles.totalItems
              to.meta.domainObjectRoles = domainObjectRoles.items
            }
          },
          props: (route) => ({
            query: route.query.q,
            page: route.meta.page,
            perPage: route.meta.perPage,
            totalItems: route.meta.totalItems,
            domainObject: route.meta.domainObject,
            domainObjectRoles: route.meta.domainObjectRoles
          }),
          beforeEnter: guardWithErrorHandling(async (to, from, next) => {
            await to.meta.beforeLoad(to, from)
            next()
          })
        },
        {
          path: 'role-sets',
          name: 'DomainObjectRoleSetsTab',
          component: DomainObjectRoleSetsTab,
          meta: {
            scrollBehavior: ScrollBehavior.tabs(),
            async beforeLoad (to, from) {
              to.meta.page = Number(to.query.page) || 1
              to.meta.perPage = 10

              const domainObjectRoleSets = await BennuAdminAPI.listDomainObjectRoleSets({
                objectId: to.params.domainObjectId,
                query: to.query.q,
                page: to.meta.page,
                perPage: to.meta.perPage
              })

              to.meta.totalItems = domainObjectRoleSets.totalItems
              to.meta.domainObjectRoleSets = domainObjectRoleSets.items
            }
          },
          props: (route) => ({
            query: route.query.q,
            page: route.meta.page,
            perPage: route.meta.perPage,
            totalItems: route.meta.totalItems,
            domainObject: route.meta.domainObject,
            domainObjectRoleSets: route.meta.domainObjectRoleSets
          }),
          beforeEnter: guardWithErrorHandling(async (to, from, next) => {
            await to.meta.beforeLoad(to, from)
            next()
          })
        }
      ]
    },
    {
      path: '/domain-browser/:domainObjectId/role-set/:roleSetName',
      name: 'DomainObjectRoleSetPage',
      component: DomainObjectRoleSetPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        async beforeLoad (to, from) {
          to.meta.page = Number(to.query.page) || 1
          to.meta.perPage = 10

          const [domainObject, relationSet] = await Promise.all([
            BennuAdminAPI.getDomainObject({
              objectId: to.params.domainObjectId
            }),
            BennuAdminAPI.getDomainObjectRoleSet({
              objectId: to.params.domainObjectId,
              roleSetName: to.params.roleSetName,
              query: to.query.q,
              page: to.meta.page,
              perPage: to.meta.perPage
            })
          ])

          // Load slots
          await Promise.all(relationSet.items.map(async (relation) => {
            if (relation.objectId) {
              const slots = await BennuAdminAPI.listDomainObjectSlots({
                objectId: relation.objectId,
                query: to.query.q,
                page: 1,
                perPage: 5
              })
              relation.slots = slots.items
            }
          }))

          to.meta.domainObject = domainObject
          to.meta.totalItems = relationSet.totalItems
          to.meta.relationSet = relationSet.items
        }
      },
      props: route => ({
        query: route.query.q,
        page: route.meta.page,
        perPage: route.meta.perPage,
        totalItems: route.meta.totalItems,
        domainObject: route.meta.domainObject,
        relationSet: route.meta.relationSet
      }),
      beforeEnter: guardWithErrorHandling(async (to, from, next) => {
        await to.meta.beforeLoad(to, from)
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

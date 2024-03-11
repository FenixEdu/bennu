<template>
  <div class="page">
    <header class="section-header page__header">
      <div class="section-header__text">
        <p class="h6 section-header__parent-page">
          <router-link :to="{ name: 'DomainObjectRoleSetsTab', params: { domainObjectId: domainObject.objectId } }">
            <span class="i-back-arrow" />
            <span>{{ getDomainObjectName(domainObject) }} - {{ domainObject.objectId }}</span>
          </router-link>
        </p>
        <h1 class="section-header__title">
          {{ $t('title', { relationSet: relationName,domainObject: getDomainObjectName(domainObject), oid: domainObject.objectId }) }}
        </h1>
        <p>{{ domainObject.type }}</p>
      </div>
    </header>

    <div
      v-if="relationSet.length > 0"
      class="card"
    >
      <table class="table">
        <thead>
          <tr>
            <th>{{ $t('table.object-id') }}</th>
            <th>{{ $t('table.type') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="role in relationSet"
            :key="role.name"
            class="table__row"
          >
            <td class="table__cell">
              <span class="u-text-strong">{{ role.objectId }}</span>
            </td>
            <td class="table__cell">
              <p>{{ role.type }}</p>
            </td>
            <td class="table__cell table__cell--right">
              <router-link
                aria-hidden="true"
                tabindex="-1"
                replace
                :to="{ name: 'DomainObjectPage', params: { domainObjectId: role.objectId } }"
              >
                <span class="i-arrow-right i--small" />
              </router-link>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <empty-state v-else>
      <p slot="title">
        {{ $t('empty-state', { relationName: relationName }) }}
      </p>
    </empty-state>

    <pagination
      :total-items="totalItems"
      :items-per-page="perPage"
      :current-page="page"
    />
  </div>
</template>

<script>
import EmptyState from '@/components/EmptyState.vue'
import Pagination from '@/components/utils/Pagination.vue'

import { guardWithErrorHandling } from '@/router/guards'

export default {
  components: {
    EmptyState,
    Pagination
  },
  beforeRouteUpdate: guardWithErrorHandling(
    async function (to, from, next) {
      this.$progress.set(10)
      await to.meta.beforeRouteLoad(to, from)
      this.$progress.complete()
      next()
    }
  ),
  props: {
    page: {
      type: Number,
      required: true
    },
    perPage: {
      type: Number,
      required: true
    },
    totalItems: {
      type: Number,
      required: true
    },
    domainObject: {
      type: Object,
      required: true
    },
    relationSet: {
      type: Array,
      required: true
    }
  },
  computed: {
    relationName () {
      return this.$route.params.roleSetName
    }
  },
  methods: {
    getDomainObjectName (domainObject) {
      return domainObject.type.split('.').pop()
    }
  },
  i18n: {
    messages: {
      pt: {
        table: {
          'object-id': 'ID do Objeto',
          type: 'Tipo'
        },
        title: 'Relation Set "{relationSet}" of "{domainObject}" - {oid}',
        'empty-state': 'Não há relações para o conjunto "{relationName}" neste objeto'
      },
      en: {
        table: {
          'object-id': 'Object ID',
          type: 'Type'
        },
        title: 'Relation Set "{relationSet}" of "{domainObject}" - {oid}',
        'empty-state': 'There are no relations for the set "{relationName}" in this object'
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.role-slots {
  padding: 0;
}

.table__cell--right {
  text-align: right;
  padding-right: 0;
}
</style>

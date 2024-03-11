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

    <form
      role="search"
      class="f-search"
    >
      <input
        ref="searchInput"
        v-model="searchInput"
        :placeholder="$t('search.placeholder')"
        class="f-search__input f-search__input--card"
        type="text"
        @keydown.enter.prevent="performSearch()"
      >
      <button
        class="f-search__submit"
        @click.prevent="performSearch()"
      >
        <span class="sr-only">
          {{ $t('search.aria-label') }}
        </span>
      </button>
    </form>

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

    <empty-state
      v-else
      style="margin-top: 2.5rem;"
    >
      <p
        v-if="hasSearchQuery"
        slot="title"
      >
        {{ $t('empty-state-search', { relationName: relationName, query: query }) }}
      </p>
      <p
        v-else
        slot="title"
      >
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
    query: {
      type: String,
      required: false,
      default: ''
    },
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
  data () {
    return {
      searchInput: ''
    }
  },
  computed: {
    relationName () {
      return this.$route.params.roleSetName
    },
    hasSearchQuery () {
      return this.$route.query.q !== undefined && this.$route.query.q !== ''
    }
  },
  methods: {
    getDomainObjectName (domainObject) {
      return domainObject.type.split('.').pop()
    },
    performSearch () {
      if (this.searchInput !== this.$route.query.q) {
        const location = { query: { q: this.searchInput === '' ? undefined : this.searchInput } }
        if (this.$route.query.q === undefined) {
          this.$router.replace(location)
        } else {
          this.$router.push(location)
        }
      }
    }
  },
  i18n: {
    messages: {
      pt: {
        search: {
          'aria-label': 'Pesquisar pelo ID do objeto',
          placeholder: 'Pesquisar pelo ID do objeto'
        },
        table: {
          'object-id': 'ID do Objeto',
          type: 'Tipo'
        },
        title: 'Relation Set "{relationSet}" of "{domainObject}" - {oid}',
        'empty-state': 'Não há relações para o conjunto "{relationName}" neste objeto',
        'empty-state-search': 'Não há relações para o conjunto "{relationName}" com a busca "{query}"'
      },
      en: {
        search: {
          'aria-label': 'Search by object ID',
          placeholder: 'Search by object ID'
        },
        table: {
          'object-id': 'Object ID',
          type: 'Type'
        },
        title: 'Relation Set "{relationSet}" of "{domainObject}" - {oid}',
        'empty-state': 'There are no relations for the set "{relationName}" in this object',
        'empty-state-search': 'There are no relations for the set "{relationName}" with the search "{query}"'
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

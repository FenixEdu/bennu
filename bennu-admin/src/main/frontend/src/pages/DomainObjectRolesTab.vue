<template>
  <div>
    <form
      role="search"
      class="f-search f-search--sm"
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

    <empty-state v-if="totalItems == 0">
      <template #title>
        <template v-if="hasSearch">
          {{ $t('empty-state.no-roles-search', { search: query }) }}
        </template>
        <template v-else>
          {{ $t('empty-state.no-roles') }}
        </template>
      </template>
    </empty-state>

    <div
      v-else
      class="card"
    >
      <table class="table">
        <thead class="table__header">
          <th class="table__heading">
            {{ $t('table.name') }}
          </th>
          <th class="table__heading">
            {{ $t('table.type') }}
          </th>
          <th class="table__heading table__cell--right">
            {{ $t('table.object-id') }}
          </th>
        </thead>
        <tbody>
          <tr
            v-for="role in domainObjectRoles"
            :key="role.name"
            class="table__row"
          >
            <td class="table__cell">
              <p class="u-text-strong">
                {{ role.name }}
              </p>
            </td>
            <td class="table__cell">
              <p>{{ role.type }}</p>
            </td>
            <td class="table__cell table__cell--right">
              <router-link
                v-if="role.objectId"
                :to="{ name:'DomainObjectPage', params: { domainObjectId: role.objectId } }"
              >
                {{ role.objectId }}
              </router-link>
              <p v-else>
                -
              </p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <pagination
      :total-items="totalItems"
      :items-per-page="perPage"
      :current-page="page"
    />
  </div>
</template>

<script>
import Pagination from '@/components/utils/Pagination.vue'
import EmptyState from '@/components/EmptyState.vue'
import { guardWithErrorHandling } from '@/router/guards'

export default {
  components: {
    Pagination,
    EmptyState
  },
  beforeRouteUpdate: guardWithErrorHandling(async function (to, from, next) {
    this.$progress.set(10)
    await to.meta.beforeRouteLoad(to, from)
    this.searchInput = to.query.q || ''
    this.$progress.complete()
    next()
  }),
  props: {
    query: {
      type: String,
      required: false,
      default: ''
    },
    perPage: {
      type: Number,
      required: true
    },
    totalItems: {
      type: Number,
      required: true
    },
    page: {
      type: Number,
      required: true
    },
    domainObjectRoles: {
      type: Array,
      required: true
    },
    domainObject: {
      type: Object,
      required: true
    },
    metadata: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      searchInput: this.query
    }
  },
  computed: {
    hasSearch () {
      return this.query !== ''
    }
  },
  methods: {
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
        table: {
          name: 'Nome',
          type: 'Tipo',
          'object-id': 'ID do Objeto'
        },
        search: {
          placeholder: 'Pesquisar...',
          'aria-label': 'Pesquisar'
        },
        'empty-state': {
          'no-roles': 'Este objeto não possui papéis.',
          'no-roles-search': 'Nenhum papel encontrado para "{search}".'
        }
      },
      en: {
        table: {
          name: 'Name',
          type: 'Type',
          'object-id': 'Object ID'
        },
        search: {
          placeholder: 'Search...',
          'aria-label': 'Search'
        },
        'empty-state': {
          'no-roles': 'This object has no roles.',
          'no-roles-search': 'No roles found for "{search}".'
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/variables";

.f-search {
  margin-bottom: 1.5rem;
}

.role-slots {
  padding: 0;
}

.table__cell--right {
  text-align: right;
}
</style>

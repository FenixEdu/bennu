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
        {{ $t('search.aria-label') }}
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
        <thead>
          <tr>
            <th>{{ $t('table.set-name') }}</th>
            <th>{{ $t('table.set-type') }}</th>
            <th class="table__cell--right">
              {{ $t('table.count') }}
            </th>
            <th />
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="roleSet in domainObjectRoleSets"
            :key="roleSet.name"
            class="table__row"
          >
            <td class="table__cell">
              <span class="u-text-strong">{{ roleSet.name }}</span>
            </td>
            <td class="table__cell">
              <p>{{ roleSet.type }}</p>
            </td>
            <td class="table__cell table__cell--right">
              <button
                v-if="counts[roleSet.name] === undefined || counts[roleSet.name].status === 'loading'"
                :disabled="counts[roleSet.name] !== undefined && counts[roleSet.name].status === 'loading'"
                class="btn btn--link btn--load"
                @click="loadCount(roleSet.name)"
              >
                {{ $t('load-count') }}
              </button>
              <p v-else>
                {{ counts[roleSet.name].count }}
              </p>
            </td>
            <td class="table__cell table__cell--right">
              <router-link
                aria-hidden="true"
                tabindex="-1"
                replace
                :to="{ name: 'DomainObjectRoleSetPage', params: { domainObjectId: domainObject.objectId, roleSetName: roleSet.name } }"
              >
                <span class="i-arrow-right i--small" />
              </router-link>
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
import { getDomainObjectRoleSetCount } from '@/api/bennu-admin'

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
    domainObject: {
      type: Object,
      required: true
    },
    metadata: {
      type: Object,
      required: true
    },
    domainObjectRoleSets: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      searchInput: this.query,
      counts: {}
    }
  },
  computed: {
    hasSearch () {
      return this.searchInput !== ''
    }
  },
  methods: {
    updateCount (roleSetName, val) {
      const newCounts = { ...this.counts }
      newCounts[roleSetName] = val
      this.counts = newCounts
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
    },
    async loadCount (roleSetName) {
      if (this.counts[roleSetName] === undefined) {
        this.updateCount(roleSetName, { status: 'loading' })
        const { count } = await getDomainObjectRoleSetCount({
          objectId: this.domainObject.objectId,
          roleSetName
        })
        this.updateCount(roleSetName, { count, status: 'finished' })
        this.counts[roleSetName].status = 'finished'
      }
    }
  },
  i18n: {
    messages: {
      pt: {
        'load-count': 'Carregar',
        table: {
          'set-name': 'Nome do conjunto',
          'set-type': 'Tipo do conjunto',
          count: 'Contagem'
        },
        search: {
          placeholder: 'Pesquisar...',
          'aria-label': 'Pesquisar'
        },
        'empty-state': {
          'no-roles': 'Este objeto de domínio não possui nenhum conjunto de papéis.',
          'no-roles-search': 'Nenhum conjunto de papéis encontrado para a pesquisa "{search}".'
        }
      },
      en: {
        'load-count': 'Load',
        table: {
          'set-name': 'Set Name',
          'set-type': 'Set Type',
          count: 'Count'
        },
        search: {
          placeholder: 'Search...',
          'aria-label': 'Search'
        },
        'empty-state': {
          'no-roles': 'This domain object has no role sets.',
          'no-roles-search': 'No role sets found for the search "{search}".'
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.f-search {
  margin-bottom: 1.5rem;
}

.table__cell--right {
  text-align: right;
  padding-right: 0;
}

.btn--load {
  margin-left: auto;
}
</style>

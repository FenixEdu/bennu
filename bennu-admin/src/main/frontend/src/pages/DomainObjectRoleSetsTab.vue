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
          {{ $t('empty-state.no-roles-search', { search: searchInput }) }}
        </template>
        <template v-else>
          {{ $t('empty-state.no-roles') }}
        </template>
      </template>
    </empty-state>

    <ol v-else>
      <li
        v-for="roleSet in domainObjectRoleSets"
        :key="roleSet.name"
        class="card"
      >
        <div class="card-row card-row--sm">
          <div class="card-row__text">
            <p>
              <span class="h4 h4--ssp">{{ roleSet.name }}</span>
            </p>
            <p>{{ roleSet.type }}</p>
          </div>
          <div class="card-row__meta">
            <router-link
              aria-hidden="true"
              tabindex="-1"
              replace
              :to="{ name: 'DomainObjectRoleSetPage', params: { domainObjectId: domainObject.objectId, roleSetName: roleSet.name } }"
            >
              <span class="i-arrow-right" />
            </router-link>
          </div>
        </div>
      </li>
    </ol>

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
  beforeRouteUpdate: guardWithErrorHandling(
    async function (to, from, next) {
      this.$progress.set(10)
      await to.meta.beforeRouteLoad(to, from)
      this.searchInput = to.query.q || ''
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
      searchInput: this.query
    }
  },
  computed: {
    hasSearch () {
      return this.searchInput !== ''
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
</style>

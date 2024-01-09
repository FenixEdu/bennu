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

    <ol v-if="relationSet.length > 0">
      <li
        v-for="role in relationSet"
        :key="role.name"
        class="card"
      >
        <div class="card-row card-row--sm">
          <div class="card-row__text">
            <p>
              <span class="h4 h4--ssp">{{ role.objectId }}</span>
            </p>
            <p>{{ role.type }}</p>
          </div>
          <div
            v-if="role.objectId"
            class="card-row__meta"
          >
            <router-link
              aria-hidden="true"
              tabindex="-1"
              :to="{ name: 'DomainObjectPage', params: { domainObjectId: role.objectId }, query: { q: undefined } }"
            >
              <span class="i-arrow-right" />
            </router-link>
          </div>
        </div>
        <div
          v-if="role.slots && role.slots.length > 0"
          class="card-row role-slots"
        >
          <ol class="card-row__text">
            <domain-object-slot-row
              v-for="slot in role.slots"
              :key="slot.name"
              :domain-object-slot="slot"
              :show-type="false"
            />
          </ol>
        </div>
      </li>
    </ol>

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
import DomainObjectSlotRow from '@/components/domain-object/DomainObjectSlotRow.vue'
import { guardWithErrorHandling } from '@/router/guards'

export default {
  components: {
    EmptyState,
    Pagination,
    DomainObjectSlotRow
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
        title: 'Relation Set "{relationSet}" of "{domainObject}" - {oid}',
        'empty-state': 'Não há relações para o conjunto "{relationName}" neste objeto'
      },
      en: {
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
</style>

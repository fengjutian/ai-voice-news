import { request } from '@umijs/max';

export async function getUsers(
  params: {
    // query
    /** 当前的页码 */
    current?: number;
    /** 页面的容量 */
    pageSize?: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.RuleList>('/api/users', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

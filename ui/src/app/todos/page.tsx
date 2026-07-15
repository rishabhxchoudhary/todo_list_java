'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { apiFetch, tokenStore, logout } from 'lib/api';
import { join } from 'node:path';

type Todo = { id: string; name: string; finished: boolean };

export default function TodosPage() {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [newTodo, setNewTodo] = useState('');
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  // Auth guard: no token → bounce to login
  useEffect(() => {
    if (!tokenStore.getAccess()) {
      router.push('/login');
      return;
    }
    loadTodos();
  }, []);

  async function loadTodos() {
    try {
      const data = await apiFetch('/api/v1/todos', 'GET', undefined);
      setTodos(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

  async function addTodo(e: React.FormEvent) {
    e.preventDefault();
    if (!newTodo.trim()) return;
    try {
      await apiFetch('/api/v1/todos', 'POST', { name: newTodo });
      setNewTodo('');
      loadTodos();
    } catch (err) {
      console.error(err);
    }
  }

  async function toggleTodo(id: string) {
    try {
      await apiFetch(`/api/v1/todos/${id}/toggle`, 'PATCH', undefined);
      loadTodos();
    } catch (err) {
      console.error(err);
    }
  }

  async function deleteTodo(id: string) {
    try {
      await apiFetch(`/api/v1/todos/${id}`, 'DELETE', undefined);
      loadTodos();
    } catch (err) {
      console.error(err);
    }
  }

  async function handleLogout() {
    await logout();
    router.push('/login');
  }

  if (loading) return <div className="p-8">Loading…</div>;

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-md mx-auto space-y-4">
        <div className="flex justify-between items-center">
          <h1 className="text-xl font-semibold">My Todos</h1>
          <button onClick={handleLogout} className="text-sm underline">Log out</button>
        </div>

        <form onSubmit={addTodo} className="flex gap-2">
          <input
            className="flex-1 border rounded px-3 py-2"
            placeholder="New todo"
            value={newTodo}
            onChange={(e) => setNewTodo(e.target.value)}
          />
          <button type="submit" className="bg-black text-white rounded px-4">Add</button>
        </form>

        <ul className="space-y-2">
          {todos.map((todo) => (
            <li key={todo.id} className="flex items-center gap-2 bg-white p-3 rounded shadow">
              <input
                type="checkbox"
                checked={todo.finished}
                onChange={() => toggleTodo(todo.id)}
              />
              <span className={todo.finished ? 'line-through text-gray-400 flex-1' : 'flex-1'}>
                {todo.name}
              </span>
              <button onClick={() => deleteTodo(todo.id)} className="text-red-600 text-sm">
                Delete
              </button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
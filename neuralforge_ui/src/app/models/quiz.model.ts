export interface Quiz {
  id: string;
  creatorUserId: string;
  title: string;
  description: string;
  projectId: string;
  projectType?: string;
  questions: QuizQuestion[];
  createdAt: Date;
  lastModifiedAt: Date;
  isDeleted: boolean;
}

export interface QuizQuestion {
  id: string;
  questionText: string;
  quizId: string;
  answers: QuizAnswer[];
  questionOrder: number;
  explanation: string;
}

export interface QuizAnswer {
  id: string;
  answerText: string;
  questionId: string;
  isCorrect: boolean;
  answerOrder: number;
}

export interface QuizAttempt {
  id: string;
  quizId: string;
  quizTitle: string;
  userId: string;
  score: number;
  totalQuestions: number;
  userAnswers: QuizUserAnswer[];
  startedAt: Date;
  completedAt: Date;
}

export interface QuizUserAnswer {
  id: string;
  attemptId: string;
  questionId: string;
  questionText: string;
  selectedAnswerId: string;
  selectedAnswerText: string;
  isCorrect: boolean;
  explanation: string;
  correctAnswerId?: string;
  correctAnswerText?: string;
}

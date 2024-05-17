import { Routes } from '@angular/router';
import { ContactUsComponent } from './components/contact-us/contact-us.component';
import { AboutUsComponent } from './components/about-us/about-us.component';
import { StudentDashboardComponent } from './components/student-dashboard/student-dashboard.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';

import { authGuard } from './guards/auth/auth.guard';
import { ExplorerComponent } from './components/explorer/explorer.component';
import { clientTeacherGuard } from './guards/auth/client-teacher.guard';
import { clientUserGuard } from './guards/auth/client-user.guard';
import { TeacherSidebarComponent } from './components/teacher-sidebar/teacher-sidebar.component';
import { TeacherDashboardComponent } from './components/teacher-dashboard/teacher-dashboard.component';
import { TeachercourseviewComponent } from './components/teachercourseview/teachercourseview.component';
import { StudentSidebarComponent } from './components/student-sidebar/student-sidebar.component';
import { ContentFormComponent } from './components/teacher-dashboard/content-form/content-form.component';
import { ContentListComponent } from './components/teacher-dashboard/content-list/content-list/content-list.component';
import { CourseDetailsComponent } from './components/course-details/course-details.component';
import { CourseFormComponent } from './components/course-form/course-form.component';
import { StudentCourseComponent } from './components/student-course/student-course.component';
import { ExplorerCourseComponent } from './components/explorer-course/explorer-course.component';
import { HomeComponent } from './components/home/home.component';

export const routes: Routes = [
    {path:"contactus", component: ContactUsComponent},
    {path:"aboutus", component: AboutUsComponent},
    {path:"sidebar-student", component: StudentSidebarComponent,
        canActivate: [clientUserGuard],
        children: [
            {path: "student-courses", component: StudentDashboardComponent},
            {path: "", redirectTo: "student-courses", pathMatch: "full"},
            {path: "student-courses/:id", component: StudentCourseComponent}
    ]
    },
   
    {path:"sidebar-teacher", component: TeacherSidebarComponent,
        canActivate: [clientTeacherGuard],
        children: [
            {path: "analytics", component: TeacherDashboardComponent},
            {path: "teacher-courses", component: TeachercourseviewComponent},
            {path: "contents", component: ContentListComponent},
            {path: "edit-content/:id", component: ContentFormComponent},
            {path: "course-details/:id", component: CourseDetailsComponent},
            {path: "course-form/:id", component: CourseFormComponent},
            {path: "", redirectTo: "analytics", pathMatch: "full"},
            {path: "new-content", component: ContentFormComponent},
            {path: "course-form", component: CourseFormComponent},
        ]
    },
    {path:"explorer", component: ExplorerComponent},
    {path:"dashboard-teacher", component: TeacherDashboardComponent, canActivate: [clientTeacherGuard]},
    {path:"dashboard-student", component: StudentDashboardComponent, canActivate: [clientUserGuard]},
    {path:"profile", component: UserProfileComponent, canActivate: [authGuard]},
    {path:"explorer-course/:id", component: ExplorerCourseComponent},
    {path:"", component:HomeComponent},
    

];